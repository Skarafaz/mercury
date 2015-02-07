# Mercury-SSH Commander

Mercury-SSH acts like a remote for your servers, sending pre-configured commands through SSH.

## Usage

Mercury-SSH reads configuration data from standard JSON files saved in the external storage.
Each configuration file must contain a valid JSON object defining a server and its commands.
Simply put your configuration files in /sdcard/Mercuty-SSH.

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

*NOTE:* each file must be UTF-8/16/32 encoded and must have `.json` extension.

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