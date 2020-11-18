import * as koapModule from '@juullabs/koap'
const koap = koapModule.com.juul.koap

import * as kotlin from '@juullabs/koap/node_modules/kotlin'

test('encode :: TCP GET empty payload :: correct encoding', () => {
  
  const method = koap.Message.Code.Method.GET
  const token = new kotlin.Long(66)
  const emptyPayload = new Uint8Array()
  const optionsArray = [
    new koap.Message.Option.UriPath('info'),
    new koap.Message.Option.UriQuery('batt')
  ]
  const optionsList = new kotlin.kotlin.collections.ArrayList(optionsArray)

  const message = new koap.Message.Tcp(
    method,
    token,
    optionsList,
    emptyPayload
  )
  const encoded = koap.encode(message)
  
  expect(encoded).toEqual(new Int8Array([0xA1, 0x01, 0x42, 0xB4, 0x69, 0x6E, 0x66, 0x6F, 0x44, 0x62, 0x61, 0x74, 0x74]));
});

test('encodeHeader :: UDP header :: correct encoding', () => {

  const mType = koap.Message.Udp.Type.Confirmable
  const method = koap.Message.Code.Method.GET
  const token = new kotlin.Long(0xCAFE)
  const id = 0xFEED
  const emptyPayload = new Uint8Array()
  const optionsArray = [
    new koap.Message.Option.UriPath('example')
  ]
  const optionsList = new kotlin.kotlin.collections.ArrayList(optionsArray)
  const message = new koap.Message.Udp(
    mType,
    method,
    id,
    token,
    optionsList,
    emptyPayload
  )

  const encodedHeader = koap.encodeHeader(message)
  
  expect(encodedHeader).toEqual(new Int8Array([0x42, 0x01, 0xFE, 0xED, 0xCA, 0xFE]))
});
