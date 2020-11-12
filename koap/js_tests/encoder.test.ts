let koapModule = require('../build/distributions/koap')
const koap = koapModule.com.juul.koap

let kotlinModule = require('../build/publications/npm/js/node_modules/kotlin')
const kotlin = kotlinModule

test('encode :: TCP GET empty payload :: correct encoding', () => {
  
  let method = koap.Message.Code.Method.GET
  let token = new kotlin.Long(66)
  let emptyPayload = new Uint8Array()
  let optionsArray = [
    new koap.Message.Option.UriPath('info'),
    new koap.Message.Option.UriQuery('batt')
  ]
  let optionsList = new kotlin.kotlin.collections.ArrayList(optionsArray)

  let message = new koap.Message.Tcp(
    method,
    token,
    optionsList,
    emptyPayload
  )
  let encoded = koap.encode(message)
  
  expect(encoded).toEqual(new Int8Array([0xA1, 0x01, 0x42, 0xB4, 0x69, 0x6E, 0x66, 0x6F, 0x44, 0x62, 0x61, 0x74, 0x74]));
});

test('encodeHeader :: UDP header :: correct encoding', () => {

  let mType = koap.Message.Udp.Type.Confirmable
  let method = koap.Message.Code.Method.GET
  let token = new kotlin.Long(0xCAFE)
  let id = 0xFEED
  let emptyPayload = new Uint8Array()
  let optionsArray = [
    new koap.Message.Option.UriPath('example')
  ]
  let optionsList = new kotlin.kotlin.collections.ArrayList(optionsArray)
  let message = new koap.Message.Udp(
    mType,
    method,
    id,
    token,
    optionsList,
    emptyPayload
  )

  let encodedHeader = koap.encodeHeader(message)
  
  expect(encodedHeader).toEqual(new Int8Array([0x42, 0x01, 0xFE, 0xED, 0xCA, 0xFE]))
});
