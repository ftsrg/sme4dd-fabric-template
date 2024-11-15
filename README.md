# Hyperledger Fabric Project Template

This repository contains template-like artifacts to quickly bootstrap a development environment and project to develop Hyperledger Fabric chaincodes.

# Pre-requisites

Make sure the suggested versions of the following tools are installed properly on your development machine (if you are developing locally on a single machine) or remote server (if you are using a remote development setup, e.g., in the BME Cloud) that will run the Fabric network and relatied tooling.

> The BME Cloud VM instance already have the following toolings installed!

## General utilities (curl, jq)
```sh
sudo apt-get update
sudo apt-get install -y curl jq
```


## Docker (version 26.0.1)
```sh
# install with utility script
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh ./get-docker.sh

# configure rootless mode
sudo groupadd docker
sudo usermod -aG docker $USER
newgrp docker

# test rootless installation
docker run hello-world
```

## Docker-compose standalone (version 2.26.1)
```sh
# install with utility script
sudo curl -SL https://github.com/docker/compose/releases/download/v2.26.1/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# test installation
docker-compose version
```

## Node Version Manager (version 0.39.7)
```sh
# install with utility script
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash

# logout and login to restart session

# test installation
nvm -v
```

## Node.JS (version 20.12.2)
```sh
# install with nvm
nvm install v20

# test installation
node -v
```

## SDKMAN! (version 5.18.2)
```sh
# install with utility script
curl -s "https://get.sdkman.io" | bash

# logout and login to restart session

# test installation
sdk version
```

## Java JDK (Eclipse Temurin version 11.0.23)
```sh
# install with SDKMAN!
sdk install java 11.0.23-tem

# test installation
javac -version
```

## Fablo (version 2.0.0)
```sh
# install locally
curl -Lf https://github.com/hyperledger-labs/fablo/releases/download/2.0.0/fablo.sh -o ./fablo && chmod +x ./fablo

# test installation
./fablo version

# warmup/pre-pull Fabric Docker images
mkdir fablo-test && cd fablo-test && ./fablo init node rest && ./fablo up && ./fablo prune && cd .. && rm -rf fablo-test
```

# VSCode Remote Development for the BME Cloud

Make sure you use an up-2-date Visual Studio Code installation on your local machine (e.g., March 2024 update, version 1.88) and install the "Remote Development" extension pack (also useful for WSL-based development).

The "Docker" extension will also come in handy when inspecting the running Fabric network.

Have the SSH connection details of your remote VM ready (the left side of the VM dashboard in BME Cloud). Perform the following steps on your __local machine__.

## Setup password-based connection
This is an easier setup, but provides a more cumbersome user experience since it prompts for the password for major commands.

1. Execute the "Add new SSH host" command (press F1 to search)
1. Enter the following connection command, substituting `<CUSTOM PORT>` with your VMs public SSH port, displayed on the VM dashboard
    * `ssh -o StrictHostKeyChecking=no cloud@vm.niif.cloud.bme.hu -p <CUSTOM PORT>`
1. Execute the "Connect to host" command (press F1 to search), select the previously saved `vm.niif...` host and input the VMs SSH connection password.

## Setup key-based connection
This setup requires more steps, but provides a more fluid user experience later on.

1. Generate a new SSH key-pair (use the `/home/youruser/.ssh/sme4dd-vm` target path if possible, or adjust the following steps accordingly)
    * `ssh-keygen -t ed25519 -C "sme4dd-vm"`
1. Copy it to the remote VM (substitute `<CUSTOM PORT>` with your VMs public SSH port, displayed on the VM dashboard)
    * `ssh-copy-id -i ~/.ssh/sme4dd-vm cloud@vm.niif.cloud.bme.hu -p <CUSTOM PORT>`
1. Add the following configuration to the SSH config file (`~/.ssh/config`, or OS-specific variants). Substitute `<CUSTOM PORT>` with your VMs public SSH port, displayed on the VM dashboard. Use the __absolute path__ to your SSH key (e.g. `C:\Users\TestUser\.ssh\sme4dd-vm` on Windows).
    ```
    Host vm.niif.cloud.bme.hu
        HostName vm.niif.cloud.bme.hu
        IdentityFile <SSH DIR ABSOLUTE PATH>\sme4dd-vm
        IdentitiesOnly yes
        User cloud
        Port <CUSTOM PORT>
    ```

If you have done everything right, VS Code is now connected to the remote VM, indicated by the `SSH: vm.niif.cloud.bme.hu` green connection status label at the bottom left corner of the IDE.

# GitHub repository access

Fork this repository to work on your own copy without interfering with others.

You probably do not want to use your everday GitHub keys/tokens if you are working in the BME Cloud environment since you do not control that environment. The best practice in such cases is to create a repository- and server-scoped deployment key that cannot access any other GitHub resources. 

Perform the following steps on the __remote VM__:
1. Generate a new SSH key-pair (use the `~/.ssh/sme4dd-gh` target path if possible, or adjust the following steps accordingly)
    * `ssh-keygen -t ed25519 -C "sme4dd-gh"`
1. Configure the connection to GitHub to use that key, i.e., add the following to the `/home/cloud/.ssh/config` file:
    ```
    Host github.com
        Hostname github.com
        IdentityFile=/home/cloud/.ssh/sme4dd-gh
    ```
1. Take note of your public key, you will need it in the next step
    * `cat /home/cloud/.ssh/sme4dd-gh.pub`

Finally, add your public key to your repository under GitHub's `Settings/Deploy Keys/Add deploy key` menu, also checking the __Allow write access__ box to be able to push your progress back to GitHub.


Now you can simply exeecute git command targeting your public/private project repository, so clone it using the terminal in VS Code:

```sh
# example SSH-based access, use your own URL
git clone git@github.com:aklenik/fabric-project-template.git
```

# Accessing network services

Make sure that the following host ports are available externally to facilitate manual testing (the ports are already opened on the BME Cloud VM instances):
* 8800: Fablo REST endpoint for the orderer organization
* 8801: Fablo REST endpoint for the first organization
* 8802: Fablo REST endpoint for the second organization
* 5100: CouchDB dashboard for the first organization
* 5120: CouchDB dashboard for the second organization
* 7010: Hyperledger Explorer dashboard

# References

* [Fablo documentation](https://github.com/hyperledger-labs/fablo)
* [Fablo REST API documentation](https://github.com/fablo-io/fablo-rest)
