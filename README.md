# Mercury-SSH Commander

Simple Android app intended to send pre-configured commands to remote servers through SSH.

## Usage

Mercury-SSH reads configuration data from standard [JSON](http://en.wikipedia.org/wiki/JSON) files saved on the device's external storage. Simply put your configuration files into the folder named *Mercury-SSH*, the app will do the rest.

## Configuration file format

Each configuration file must be an UTF-8/16/32 encoded text file with `.json` extension, containing a valid JSON object defining a server and its commands according to the following specifications.

Server property summary:

Property | Type | Notes | Description
---------|------|-------|------------
`name` | string | optional, defaults to `"Server"` | Friendly name (used as label).
`host` | string | mandatory | Target hostname or IP address.
`port` | integer (1-65535) | optional, defaults to 22 | Target port.
`user` | string | mandatory | Login user.
`password` | string | mandatory | Login password.
`commands` | array | optional | An array of objects defining available commands for the server. Read more for details.

Command property summary:

Property | Type | Notes | Description
---------|------|-------|------------
`name` | string | optional, defaults to `"Command"` | Friendly name (used as label).
`sudo` | boolean | optional, defaults to `false` | State if the command needs to be executed as root.
`cmd` | string | mandatory | The command itself.

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

## Limitations

This app is intended to be used as a remote so interactive commands and output handling are not supported.
