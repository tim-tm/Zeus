# Zeus Client #
### The open-source cheating solution ###

## How to use ## 
Either compile the source code by yourself or download pre-build binaries.

## Notice ##
This client has only been, and only will be tested on Linux.

## Contribution ##
- Feel free to open pull requests for either additional features or bug-fixes. 
Please follow code-style.
- You can also contribute my making scripts or configs.

## ScriptAPI-Docs ##
### General Note ###
All scripts are currently written in JavaScript.
The location to save scripts is in your minecraft folder under `assets/minecraft/zeus/script`.
In order to successfully add a script, type out the 'bootstrap' method as shown below.

`function bootstrap() { return ["Name", "Description", "Author"] }`
This line must be included in all scripts.

### Events ###
Packet-Event 
- Params: 
  - packetName - Java-Class Name, "me.tim.ZeusClient"
  - state: Packet-State - "SEND" or "RECEIVE"
- Return-Value:
  - isCancelled? - should the Packet be cancelled?, true if yes, false if no.

`function onPacket(packetName, state) {
    return true
}`

More Events soon...
