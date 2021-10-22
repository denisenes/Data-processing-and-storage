import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class Server {

    private final int LPORT;
    private final int RPORT;
    private final String IP;

    private Selector selector;
    private int connections_n;

    Server(int lport, String ip, int rport) {
        LPORT = lport;
        IP = ip;
        RPORT = rport;
        connections_n = 0;
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);

        InetSocketAddress port = new InetSocketAddress(LPORT);
        channel.socket().bind(port);

        channel.register(selector, SelectionKey.OP_ACCEPT);
        while (selector.select() >= 1) {
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();

            while (it.hasNext()) {
                SelectionKey selectionKey = it.next();
                it.remove();
                if (selectionKey.isAcceptable()) {
                    System.out.println("Someone wants to be accepted");
                    acceptHandler(selectionKey);
                } else if (selectionKey.isReadable()) {
                    if (!((IOContext) selectionKey.attachment()).read()) {
                        selectionKey.channel().close();
                        selectionKey.cancel();
                        connections_n--;
                        System.out.println("Connection closed. Total connections: " + connections_n);
                    }
                } else if (selectionKey.isWritable()) {
                    if (((IOContext) selectionKey.attachment()).write()) {
                        selectionKey.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
        }
    }

    private void acceptHandler(SelectionKey key) {
        ServerSocketChannel fromClient = (ServerSocketChannel) key.channel();
        try {
            // accept request from client
            SocketChannel channel = fromClient.accept();
            connections_n++;
            System.out.println("Accepted connection:" + channel.socket() + "Total connections: " + connections_n);

            // register read operations on this channel
            channel.configureBlocking(false);
            SelectionKey clientKey = channel.register(selector, SelectionKey.OP_READ);

            // open connection to the server
            InetSocketAddress address = new InetSocketAddress(IP, RPORT);
            SocketChannel toServer = SocketChannel.open(address);
            toServer.configureBlocking(false);
            SelectionKey serverKey = toServer.register(selector, SelectionKey.OP_READ);

            // create new buffers
            int BUFFER_SIZE = 1024;
            ByteBuffer buffer1 = ByteBuffer.allocate(BUFFER_SIZE);
            ByteBuffer buffer2 = ByteBuffer.allocate(BUFFER_SIZE);

            // attach contexts to selection keys
            IOContext context1 = new IOContext(serverKey, buffer1, buffer2, clientKey);
            IOContext context2 = new IOContext(clientKey, buffer2, buffer1, serverKey);
            serverKey.attach(context1);
            clientKey.attach(context2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class IOContext {
        private final SocketChannel channel;
        private final ByteBuffer r_buff, w_buff;

        private final SelectionKey neighbourKey;

        IOContext(SelectionKey channelKey, ByteBuffer b1, ByteBuffer b2, SelectionKey nk) {
            this.channel = (SocketChannel) channelKey.channel();
            r_buff = b1; w_buff = b2;
            this.neighbourKey = nk;
        }

        public boolean write() throws IOException {
            w_buff.flip();
            channel.write(w_buff);
            return w_buff.remaining() == 0;
        }

        public boolean read() throws IOException {
            r_buff.clear();
            if(channel.read(r_buff) == -1){
                neighbourClose();
                return false;
            }
            IOContext neighbourContext = (IOContext) neighbourKey.attachment();
            if(!neighbourContext.write()) {
                neighbourContext.neighbourKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            }
            return true;
        }

        private void neighbourClose() {
            try {
                neighbourKey.channel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            neighbourKey.cancel();
        }
    }
}