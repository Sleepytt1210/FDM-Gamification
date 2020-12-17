# SSH Tunneling
1. Create the file sshConfig.properties in src/main/resources
2. Fill in the values
```properties
ssh.tunnel.url=cs-linux.ncl.ac.uk
ssh.tunnel.username=YOUR_UNI_USERNAME
ssh.tunnel.password=YOUR_UNI_PASSWORD
ssh.tunnel.port=22
```

# Run the project
Go to terminal and run command
`gradlew bootRun`