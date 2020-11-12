// import koapModule from '../build/distributions/koap'
import koapModule from '../build/publications/npm/js/koap-koap'
//let koapModule = require('../build/distributions/koap')

//console.log(koap)

const koap = koapModule.com.juul.koap

let kotlinModule = require('../build/publications/npm/js/node_modules/kotlin')
const kotlin = kotlinModule

test('decodeTcp :: TCP payload :: correctly decoded', () => {
  
    let encodedMessage = new Uint8Array([0xD1, 0x04, 0x02, 0x42, 0xB4, 0x69, 0x6E, 0x66, 0x6F, 0x44, 0x62, 0x61, 0x74, 0x74, 0xFF, 0x42, 0x61, 0x74, 0x6D, 0x61, 0x6E])

    let decodedMessage = koap.decodeTcp(encodedMessage)
    
    expect(decodedMessage.code).toEqual(koap.Message.Code.Method.POST)
    expect(decodedMessage.token).toEqual(new kotlin.Long(66))
    expect(decodedMessage.options.modCount).toEqual(2)
    expect(decodedMessage.payload).toEqual(new Uint8Array([0x42, 0x61, 0x74, 0x6d, 0x61, 0x6e]))
});

test('decodeUdp :: UDP payload :: correctly decoded', () => {
  
    let encodedMessage = new Uint8Array([0x42, 0x01, 0xFE, 0xED, 0xCA, 0xFE, 0xB5, 0x2F, 0x74, 0x65, 0x73, 0x74, 0xFF, 0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0x55, 0x44, 0x50, 0x21])

    let decodedMessage = koap.decodeUdp(encodedMessage)
    
    expect(decodedMessage.type).toEqual(koap.Message.Udp.Type.Confirmable)
    expect(decodedMessage.code).toEqual(koap.Message.Code.Method.GET)
    expect(decodedMessage.id).toEqual(0xFEED)
    expect(decodedMessage.token).toEqual(new kotlin.Long(0xCAFE))
    expect(decodedMessage.options.modCount).toEqual(1)
    expect(decodedMessage.payload).toEqual(new Uint8Array([0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x55, 0x44, 0x50, 0x21]))
});

test('decodeWithUdpHeader :: UDP payload :: returns header correctly', () => {

    let encodedMessage = new Uint8Array([0x42, 0x01, 0xFE, 0xED, 0xCA, 0xFE, 0xB5, 0x2F, 0x74, 0x65, 0x73, 0x74, 0xFF, 0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0x55, 0x44, 0x50, 0x21])

    let mType = koap.Message.Udp.Type.Acknowledgement
    let method = koap.Message.Code.Method.POST
    let token = new kotlin.Long(0xCAFE)
    let id = 0xFEED
    let header = new koap.Header.Udp(
        12,
        1,
        mType,
        method,
        id,
        token
    )

    let decodedMessage = koap.decodeWithUdpHeader(encodedMessage, header)

    expect(decodedMessage.type).toEqual(koap.Message.Udp.Type.Acknowledgement)
    expect(decodedMessage.code).toEqual(koap.Message.Code.Method.POST)
    expect(decodedMessage.id).toEqual(0xFEED)
    expect(decodedMessage.token).toEqual(new kotlin.Long(0xCAFE))
    expect(decodedMessage.options.modCount).toEqual(0)
    expect(decodedMessage.payload).toEqual(new Uint8Array([0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x55, 0x44, 0x50, 0x21]))
});

test('decodeWithTcpHeader :: TCP payload :: returns header correctly', () => {

    let encodedMessage = new Uint8Array([0xD1, 0x04, 0x02, 0x42, 0xB4, 0x69, 0x6E, 0x66, 0x6F, 0x44, 0x62, 0x61, 0x74, 0x74, 0xFF, 0x42, 0x61, 0x74, 0x6D, 0x61, 0x6E])

    let length = new kotlin.Long(7)
    let method = koap.Message.Code.Method.POST
    let token = new kotlin.Long(0xCAFE)
    let header = new koap.Header.Tcp(
        14,
        length,
        method,
        token
    )

    let decodedMessage = koap.decodeWithTcpHeader(encodedMessage, header)

    expect(decodedMessage.code).toEqual(koap.Message.Code.Method.POST)
    expect(decodedMessage.token).toEqual(new kotlin.Long(0xCAFE))
    expect(decodedMessage.options.modCount).toEqual(0)
    expect(decodedMessage.payload).toEqual(new Uint8Array([0x42, 0x61, 0x74, 0x6D, 0x61, 0x6E]))
});

test('decodeUdpHeader :: UDP payload :: returns header correctly', () => {

    let encodedMessage = new Uint8Array([0x42, 0x01, 0xFE, 0xED, 0xCA, 0xFE, 0xB5, 0x2F, 0x74, 0x65, 0x73, 0x74, 0xFF, 0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0x55, 0x44, 0x50, 0x21])

    let decodedHeader = koap.decodeUdpHeader(encodedMessage)

    expect(decodedHeader.size).toEqual(6)
    expect(decodedHeader.version).toEqual(1)
    expect(decodedHeader.type).toEqual(koap.Message.Udp.Type.Confirmable)
    expect(decodedHeader.code).toEqual(koap.Message.Code.Method.GET)
    expect(decodedHeader.messageId).toEqual(0xFEED)
    expect(decodedHeader.token).toEqual(new kotlin.Long(0xCAFE))
});

test('decodeTcpHeader :: TCP payload :: returns header correctly', () => {

    let encodedMessage = new Uint8Array([0xD1, 0x04, 0x02, 0x42, 0xB4, 0x69, 0x6E, 0x66, 0x6F, 0x44, 0x62, 0x61, 0x74, 0x74, 0xFF, 0x42, 0x61, 0x74, 0x6D, 0x61, 0x6E])

    let decodedHeader = koap.decodeTcpHeader(encodedMessage)

    expect(decodedHeader.size).toEqual(4)
    expect(decodedHeader.length).toEqual(new kotlin.Long(17))
    expect(decodedHeader.code).toEqual(koap.Message.Code.Method.POST)
    expect(decodedHeader.token).toEqual(new kotlin.Long(66))
});