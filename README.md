# torrent_tools
一款java编写的BT种子工具，支持多种功能，包括BT种子解析、生成、编辑、磁力生成等功能
# 注意
此程序使用JDK 17编写，如果使用低版本JDK可能会出现问题
# 开始使用
## 方式1
使用idea打开 配置入口程序com.github.lovept.App参数使用`--help`查看帮助
## 方式2
使用maven打包 运行命令：
```
java -jar target/torrent_tools-1.0-SNAPSHOT-jar-with-dependencies.jar --help
```
目前所支持的命令行参数如下:
```
TorrentTools CLI
Tools for inspecting, creating and modifying bittorrent metafiles.

Usage: torrent [-hV] [COMMAND]

Options:
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.

SubCommands:
  info    General information about bittorrent metafiles.
  edit    Edit the torrent file.
  create  Create a torrent file for the specified file.
  magnet  Getting a magnetic link to a seed file

Developed by ❤️lovept :)
```
子命令`info`:
```
General information about bittorrent metafiles.
Usage: torrent info [-hV] [--raw] <inputFile>
      <inputFile>   .torrent file path.

Options:
  -h, --help        Show this help message and exit.
      --raw         Print the metafile data formatted as JSON.
  -V, --version     Print version information and exit.
```

子命令`edit`:
```
Edit the torrent file.
Usage: torrent edit [-hV] [--no-announce] [--no-created-by]
                    [--no-creation-date] [--no-publisher] [--no-source]
                    [-a=<announceUrl>] [-c=<comment>] [--create-by=<createBy>]
                    [-d=<creationDate>] [-n=<torrentName>] [-o=<outputPath>]
                    [-p=<privateFlag>] [--publisher=<publisher>] [-s=<source>]
                    <inputFile>
      <inputFile>            edit the torrent file.

Options:
  -a, --announce=<announceUrl>
                             Add one or multiple announces urls.
  -c, --comment=<comment>    Add a comment.
      --create-by=<createBy> Override the value of the created by field.
  -d, --creation-date=<creationDate>
                             Override the value of the creation date field as
                               ISO-8601 time or POSIX time.
  -h, --help                 Show this help message and exit.
  -n, --name=<torrentName>   Set the name of the torrent. This changes the
                               filename for single file torrents.
      --no-announce          Do not include the announce tag.
      --no-created-by        Do not include the name and version of this
                               program.
      --no-creation-date     Do not include the creation date.
      --no-publisher         Do not include the publisher.
      --no-source            Do not include the source tag.
  -o, --output=<outputPath>  Set the filename and/or output directory of the
                               created file.
                             [default: <name>.torrent]
                             This will overwrite the existing file if the name
                               is the same.
                             Use a path with trailing slash to only set the
                               output directory.

  -p, --private=<privateFlag>
                             Set the private flag to disable DHT and PEX.
      --publisher=<publisher>
                             Override the value of the publisher field.
  -s, --source=<source>      Add a source tag to facilitate cross-seeding.
  -V, --version              Print version information and exit.

```

子命令`create`:
```
Create a torrent file for the specified file.
Usage: torrent create [-hpV] [--no-created-by] [--no-creation-date]
                      [--no-publisher] [--no-source] [-a=<announceUrl>]
                      [-c=<comment>] [--created-by=<createdBy>]
                      [-d=<creationDate>] [-l=<pieceLength>] [-n=<torrentName>]
                      [-o=<outputPath>] [--publisher=<publisher>] [-s=<source>]
                      <inputFile>
      <inputFile>            The file to create a torrent for.

Options:
  -a, --announce=<announceUrl>
                             Add one or multiple announces urls.
  -c, --comment=<comment>    Add a comment.
      --created-by=<createdBy>
                              Override the value of the created by field.
  -d, --creation-date=<creationDate>
                             Override the value of the creation date field as
                               ISO-8601 time or POSIX time.
  -h, --help                 Show this help message and exit.
  -l, --piece-size=<pieceLength>
                             Set the piece size. When no unit is specified
                               block size will be either 2^<n> bytes, or <n>
                               bytes if n is larger or equal to 16384. Piece
                               size must be a power of two in range [16K, 64M].
                               Leave empty or set to auto to determine by total
                               file size. [default: auto]
  -n, --name=<torrentName>   Set the name of the torrent. This changes the
                               filename for single file torrents.
      --no-created-by        Do not include the name and version of this
                               program.
      --no-creation-date     Do not include the creation date.
      --no-publisher         Do not include the publisher.
      --no-source            Do not include the source tag.
  -o, --output=<outputPath>  Set the filename and/or output directory of the
                               created file. [default: <name>.torrent]
  -p, --private              Set the private flag to disable DHT and PEX.
      --publisher=<publisher>
                             Add a source tag to facilitate cross-seeding.
  -s, --source=<source>      Add a source tag to facilitate cross-seeding.
  -V, --version              Print version information and exit.
```
子命令`magnet`:
```
Getting a magnetic link to a seed file
Usage: torrent magnet [-hV] <inputFile>
      <inputFile>   .torrent file path.

Options:
  -h, --help        Show this help message and exit.
  -V, --version     Print version information and exit.
```