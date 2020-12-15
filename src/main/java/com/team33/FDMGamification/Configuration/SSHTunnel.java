package com.team33.FDMGamification.Configuration;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Value("${database.rport}")
    private int rport;

    private Session session;
    private final Logger log =LoggerFactory.getLogger(SSHTunnel.class);

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
        log.info("Connected to " + host);
        int assignedPort = session.setPortForwardingL(lport, rhost, rport);

        log.info("localhost:" + assignedPort +" -> "+ rhost + ":" + rport);
        log.info("Port forwarded");

    }

    @PreDestroy
    public void shutdown() throws Exception {
        if (session != null && session.isConnected()) {
            log.info("Session closed!");
            session.disconnect();
        }
    }
}
