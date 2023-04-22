var c03 = "net.minecraft.network.play.client.C03PacketPlayer"
var c04 = "net.minecraft.network.play.client.C03PacketPlayer$C04PacketPlayerPosition"
var c05 = "net.minecraft.network.play.client.C03PacketPlayer$C05PacketPlayerLook"
var c06 = "net.minecraft.network.play.client.C03PacketPlayer$C06PacketPlayerPosLook"

function bootstrap() {
    return ["Disabler", "Goofy ahh C03 Disabler.", "Tim"]
}

function onPacket(packetName, state) {
    if (state == "SEND" && (packetName == c03 || packetName == c04 || packetName == c05 || packetName == c06)) {
        return true
    }
    return false
}