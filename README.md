# Mercury-SSH Commander

Mercury-SSH acts like a remote for your servers, sending pre-configured commands through SSH.

## Usage

Mercury-SSH reads configuration data from standard JSON files saved in the external storage.
Each file defines a server and the commands you want to send to it.
Simply put your configuration files in /sdcard/Mercuty-SSH.

## Configuration file format

Each configuration file (UTF-8 encoded, .json extension) must contain a valid JSON object representing
a server and its commands.

### Server

Property | Type | Notes | Description
---------|------|-------|------------
`name` | string | optional, default "Server" | Friendly name (used as label)
`host` | string | mandatory | Hostname or IP address
`port` | integer (1-65535) | optional, deafult 22 | Port
`user` | string | mandatory | Login user
`password` | string | mandatory | Login password
`commands` | array | optional | Array of objects defining available commands for this server. See next section for details about command objects

### Command

Property | Type | Notes | Description
---------|------|-------|------------
`name` | string | optional, default "Command" | Friendly name (used as label)
`sudo` | boolean | optional, default `false` | State if the command has to be executed as root
`cmd` | string | mandatory | The command