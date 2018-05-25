package IgniteProject.Tom2DHT;

import java.io.IOException;
import java.net.InetAddress;

import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;
public class ExampleSimple {

    final private PeerDHT peer;

    public ExampleSimple(int peerId) throws Exception {

        peer = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(peerId)).ports(4000 + peerId).start()).start();

        FutureBootstrap fb = this.peer.peer().bootstrap().inetAddress(InetAddress.getByName("193.55.163.190")).ports(4001).start();
        fb.awaitUninterruptibly();
        if(fb.isSuccess()) {
            peer.peer().discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
        }
    }

    public static void main(String[] args) throws NumberFormatException, Exception {
        ExampleSimple dns = new ExampleSimple(Integer.parseInt(args[0]));
        if (args.length == 3) {
            dns.store(args[1], args[2]);
        }
        if (args.length == 2) {
            System.out.println("Name:" + args[1] + " IP:" + dns.get(args[1]));
        }
    }

    private String get(String name) throws ClassNotFoundException, IOException {
        FutureGet futureGet = peer.get(Number160.createHash(name)).start();
        futureGet.awaitUninterruptibly();
        if (futureGet.isSuccess()) {
            return futureGet.dataMap().values().iterator().next().object().toString();
        }
        return "not found";
    }

    private void store(String name, String ip) throws IOException {
        peer.put(Number160.createHash(name)).data(new Data(ip)).start().awaitUninterruptibly();
    }
}
