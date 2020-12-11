package com.team33.FDMGamification;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@PropertySource("classpath:sshConfig.properties")
public class SSHTunnel {

    @Value("${ssh.tunnel.url}")
    private String host;

    @Value("${ssh.tunnel.username}")
    private String username;

    @Value("${ssh.tunnel.password}")
    private String password;

    @Value("${ssh.tunnel.port:22}")
    private int port;

    @Value("${database.port}")
    private int lport;

    @Value("${database.rhost}")
    private String rhost;

    private int rport = 3306;

    private Session session;

    @PostConstruct
    public void init() throws Exception {
        JSch jsch = new JSch();
        // Get SSH session
        session = jsch.getSession(username, host, port);
        session.setPassword(password);
        java.util.Properties config = new java.util.Properties();
        // Never automatically add new host keys to the host file
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        // Connect to remote server
        session.connect();
        System.out.println("Connected to " + host);
        int assignedPort = session.setPortForwardingL(lport, rhost, rport);
        System.out.println("localhost:" + assignedPort +" -> "+ rhost + ":" + rport);
        System.out.println("Port forwarded");

    }

    @PreDestroy
    public void shutdown() throws Exception {
        if (session != null && session.isConnected()) {
            System.out.println("Session closed!");
            session.disconnect();
        }
    }
}
