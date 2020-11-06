let koapModule = require('../build/distributions/koap')
const koap = koapModule.com.juul.koap

let kotlinModule = require('../build/publications/npm/js/node_modules/kotlin')
const kotlin = kotlinModule

test('TCP GET empty payload :: correct encoding', () => {
  
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
  
  //Make sure this is correct in hex?
  expect(encoded).toEqual(new Int8Array([-95, 1, 66, -76, 105, 110, 102, 111, 68, 98, 97, 116, 116]));
});

test('TCP POST empty payload :: correct encoding', () => {
  
  let method = koap.Message.Code.Method.POST
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

  // Better to not convert? Introduces extra point of failure this way
  let hexEncoded = byteArrayToHexString(encoded)
  console.log(hexEncoded)
  
  expect(encoded).toEqual(new Int8Array([0xA1, 0x02, 0x42, 0xB4, 0x69, 0x6E, 0x66, 0x6F, 0x44, 0x62, 0x61, 0x74, 0x74]));
});

test('TCP POST payload :: correct encoding', () => {
  
  let method = koap.Message.Code.Method.POST
  let token = new kotlin.Long(66)
  let payload = new Uint8Array([0x42, 0x61, 0x74, 0x6d, 0x61, 0x6e])
  let optionsArray = [
    new koap.Message.Option.UriPath('info'),
    new koap.Message.Option.UriQuery('batt')
  ]
  let optionsList = new kotlin.kotlin.collections.ArrayList(optionsArray)

  let message = new koap.Message.Tcp(
    method,
    token,
    optionsList,
    payload
  )
  let encoded = koap.encode(message)

  // Better to not convert? Introduces extra point of failure this way
  let hexEncoded = byteArrayToHexString(encoded)
  console.log(hexEncoded)
  
  expect(encoded).toEqual(new Int8Array([0xD1, 0x04, 0x02, 0x42, 0xB4, 0x69, 0x6E, 0x66, 0x6F, 0x44, 0x62, 0x61, 0x74, 0x74, 0xFF, 0x42, 0x61, 0x74, 0x6D, 0x61, 0x6E]));
});

test('UDP GET empty payload :: correct encoding', () => {

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

  let encoded = koap.encode(message)
  console.log(encoded)

  // Better to not convert? Introduces extra point of failure this way
  let hexEncoded = byteArrayToHexString(encoded)
  console.log(hexEncoded)
  
  expect(encoded).toEqual(new Int8Array([0x42, 0x01, 0xFE, 0xED, 0xCA, 0xFE, 0xB7, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65]));
});