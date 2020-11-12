let koapModule = require('../build/distributions/koap')
const koap = koapModule.com.juul.koap

let kotlinModule = require('../build/publications/npm/js/node_modules/kotlin')
const kotlin = kotlinModule

test('TCP empty payload :: correctly decoded', () => {
  
    let encodedMessage = new Uint8Array([0xA1, 0x02, 0x42, 0xB4, 0x69, 0x6E, 0x66, 0x6F, 0x44, 0x62, 0x61, 0x74, 0x74])

    let decodedMessage = koap.decodeTcp(encodedMessage)

    //console.log(decodedMessage)

    // Better to not convert? Introduces extra point of failure this way
    let decodedPayload = byteArrayToHexString(decodedMessage.payload)
    
    expect(decodedMessage.code).toEqual(koap.Message.Code.Method.POST)
    expect(decodedMessage.token).toEqual(new kotlin.Long(66))
    expect(decodedMessage.options.modCount).toEqual(2)
    expect(decodedPayload).toEqual("")
});

test('TCP payload :: correctly decoded', () => {
  
    let encodedMessage = new Uint8Array([0xD1, 0x04, 0x02, 0x42, 0xB4, 0x69, 0x6E, 0x66, 0x6F, 0x44, 0x62, 0x61, 0x74, 0x74, 0xFF, 0x42, 0x61, 0x74, 0x6D, 0x61, 0x6E])

    let decodedMessage = koap.decodeTcp(encodedMessage)

    //console.log(decodedMessage)

    // Better to not convert? Introduces extra point of failure this way
    let decodedPayload = byteArrayToHexString(decodedMessage.payload)
    
    expect(decodedMessage.code).toEqual(koap.Message.Code.Method.POST)
    expect(decodedMessage.token).toEqual(new kotlin.Long(66))
    expect(decodedMessage.options.modCount).toEqual(2)
    expect(decodedPayload).toEqual("42 61 74 6d 61 6e")
});