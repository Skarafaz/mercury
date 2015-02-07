# Mercury-SSH Commander

Simple Android app intended to execute pre-configured commands on remote servers through SSH.

## Usage

Mercury-SSH reads configuration data from standard JSON files saved in the external storage.
Each configuration file (an UTF-8/16/32 encoded text file with `.json` extension) must contain a valid
JSON object defining a server and its commands according to the following specifications:

### Server property summary

Property | Type | Notes | Description
---------|------|-------|------------
`name` | string | optional, default "Server" | Friendly name (used as label)
`host` | string | mandatory | Hostname or IP address
`port` | integer (1-65535) | optional, deafult 22 | Port
`user` | string | mandatory | Login user
`password` | string | mandatory | Login password
`commands` | array | optional | Array of objects defining available commands for this server. See next section for details about command objects

### Server property summary

Property | Type | Notes | Description
---------|------|-------|------------
`name` | string | optional, default "Command" | Friendly name (used as label)
`sudo` | boolean | optional, default `false` | State if the command has to be executed as root
`cmd` | string | mandatory | The command

Here is a sample configuration file:

```javascript
{
    "name" : "Mediaserver",
    "host" : "192.168.0.150",
    "port" : 22,
    "user" : "an",
    "password" : "12345",
    "commands" : [ {
        "name" : "Restart plex",
        "sudo" : true,
        "cmd" : "service plexmediaserver restart"
    }, {
        "name" : "Shutdown",
        "sudo" : true,
        "cmd" : "shutdown -h now"
    }, {
        "name" : "Rsync music",
        "sudo" : true,
        "cmd" : "rsync -a --delete --exclude '.@__qini' --chown=root:root --chmod=D775,F664 /mnt/nas/music/ /var/data/music/"
    } ]
}
```

Simply put your configuration files in the directory called *Mercury-SSH* in the device external storage,
the app will do the rest.