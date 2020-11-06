let koapModule = require('../build/distributions/koap')
const koap = koapModule.com.juul.koap

let kotlinModule = require('../build/publications/npm/js/node_modules/kotlin')
const kotlin = kotlinModule

test('UDP payload :: correctly decoded', () => {
  
    let encodedMessage = new Uint8Array([0x42, 0x01, 0xFE, 0xED, 0xCA, 0xFE, 0xB5, 0x2F, 0x74, 0x65, 0x73, 0x74, 0xFF, 0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0x55, 0x44, 0x50, 0x21])

    let decodedMessage = koap.decodeUdp(encodedMessage)

    console.log(decodedMessage)

    // Better to not convert? Introduces extra point of failure this way
    let decodedPayload = byteArrayToHexString(decodedMessage.payload)
    
    //expect(encoded).toEqual(new Int8Array([-95, 1, 66, -76, 105, 110, 102, 111, 68, 98, 97, 116, 116]));
    expect(decodedMessage.type).toEqual(koap.Message.Udp.Type.Confirmable)
    expect(decodedMessage.code).toEqual(koap.Message.Code.Method.GET)
    expect(decodedMessage.id).toEqual(0xFEED)
    expect(decodedMessage.token).toEqual(new kotlin.Long(0xCAFE))
    expect(decodedMessage.options.modCount).toEqual(1)
    expect(decodedPayload).toEqual("48 65 6c 6c 6f 20 55 44 50 21")
});

test('UDP empty payload :: correctly decoded', () => {
  
    let encodedMessage = new Uint8Array([0x42, 0x01, 0xFE, 0xED, 0xCA, 0xFE, 0xB5, 0x2F, 0x74, 0x65, 0x73, 0x74])

    let decodedMessage = koap.decodeUdp(encodedMessage)

    console.log(decodedMessage)

    // Better to not convert? Introduces extra point of failure this way
    let decodedPayload = byteArrayToHexString(decodedMessage.payload)
    
    //expect(encoded).toEqual(new Int8Array([-95, 1, 66, -76, 105, 110, 102, 111, 68, 98, 97, 116, 116]));
    expect(decodedMessage.type).toEqual(koap.Message.Udp.Type.Confirmable)
    expect(decodedMessage.code).toEqual(koap.Message.Code.Method.GET)
    expect(decodedMessage.id).toEqual(0xFEED)
    expect(decodedMessage.token).toEqual(new kotlin.Long(0xCAFE))
    expect(decodedMessage.options.modCount).toEqual(1)
    expect(decodedPayload).toEqual("")
});