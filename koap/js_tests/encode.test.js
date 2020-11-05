//var toTest = import("../build/publications/npm/js/koap-koap.js");
//
//toTest.then((module) => {
//           // Do something with the module.
//           console.log("Module loaded");
//         }).catch(err => {
//                   console.log(err.message);
//                 });

//import koapModule from '../build/publications/npm/js/koap-koap'

//import koapModule from '../build/distributions/koap'
let koapModule = require('../build/distributions/koap')
const koap = koapModule.com.juul.koap

let kotlinModule = require('../build/publications/npm/js/node_modules/kotlin')
const kotlin = kotlinModule
//const Long = koapModule.Kotlin.Long

//let kotlin = require('../build/kotlin/compileKotlinJs')
//import kotlin from 'kotlin'

//import koapModule from '@juullabs/koap'

//const koap = koapModule.com.juul.koap

test('empty Tcp GET payload :: correct payload', () => {
  
  let method = koap.Message.Code.Method.GET
  let token = new kotlin.Long(66)
  //console.log(koap)
  //console.log(kotlin)
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

  //console.log(encoded)
  
  expect(encoded).toEqual(new Int8Array([-95, 1, 66, -76, 105, 110, 102, 111, 68, 98, 97, 116, 116]));
});

test('empty Tcp POST payload :: correct payload', () => {
  
  let method = koap.Message.Code.Method.POST
  let token = new kotlin.Long(66)
  //console.log(koap)
  //console.log(kotlin)
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

  //console.log(encoded)
  
  expect(encoded).toEqual(new Int8Array([-95, 2, 66, -76, 105, 110, 102, 111, 68, 98, 97, 116, 116]));
});

test('readme Udp GET example :: correct payload', () => {
  
  // let type = koap.Message.Udp.Type.Confirmable
  // let method = koap.Message.Code.Method.GET
  // let token = new kotlin.Long(0xCAFE)
  // let id = 0xFEED
  // //console.log(koap)
  // //console.log(kotlin)
  // //let payload = new Uint8Array([0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0x55, 0x44, 0x50, 0x21])
  // let emptyPayload = new Uint8Array()
  // let optionsArray = [
  //   new koap.Message.Option.UriPath('example')
  // ]
  // let optionsList = new kotlin.kotlin.collections.ArrayList(optionsArray)

  // console.log(type)

  // let message = new koap.Message.Udp(
  //   type,
  //   method,
  //   id,
  //   token,
  //   optionsList,
  //   emptyPayload
  // )
  // let encoded = koap.encode(message)

  // console.log(encoded)
  
  // expect(encoded).toEqual(new Int8Array([-95, 2, 66, -76, 105, 110, 102, 111, 68, 98, 97, 116, 116]));

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

  let hexEncoded = byteArrayToHexString(encoded)
  console.log(hexEncoded)
  
  expect(encoded).toEqual(new Int8Array([0x42, 0x01, 0xFE, 0xED, 0xCA, 0xFE, 0xB7, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65]));
});