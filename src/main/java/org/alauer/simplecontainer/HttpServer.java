package org.alauer.simplecontainer;

import com.google.common.util.concurrent.AbstractIdleService;
import org.alauer.simplecontainer.servlet.ServletBridgeChannelPipelineFactory;
import org.alauer.simplecontainer.servlet.config.WebappConfiguration;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;

public class HttpServer extends AbstractIdleService {
    private final SocketAddress address;
    private final ServerBootstrap bootstrap;
    private final ServletBridgeChannelPipelineFactory servletBridge;

    private Channel channel;

    public static HttpServer createServer(int port, WebappLoader loader) throws Exception {
        loader.init();
        loader.start();

        Thread.currentThread().setContextClassLoader(loader.getClassLoader());

        return new HttpServer(
                new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()),
                new InetSocketAddress(port),
                loader.getWebappConfiguration());
    }

    public HttpServer(ChannelFactory factory, SocketAddress address, WebappConfiguration webappConfiguration) {
        this.bootstrap = new ServerBootstrap(factory);
        this.address = address;
        this.servletBridge = new ServletBridgeChannelPipelineFactory(webappConfiguration);
    }

    @Override
    protected void startUp() throws Exception {
        this.bootstrap.setPipelineFactory(this.servletBridge);
        this.bootstrap.setOption("child.tcpNoDelay", true);
        this.bootstrap.setOption("child.keepAlive", true);

        this.channel = this.bootstrap.bind(this.address);
    }

    @Override
    protected void shutDown() throws Exception {
        this.servletBridge.shutdown();
        this.channel.close().awaitUninterruptibly();
        this.bootstrap.releaseExternalResources();
    }
}
