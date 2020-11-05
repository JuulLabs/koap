global.byteToHexString = (byte) => {
    return '' + ('0' + (byte & 0xFF).toString(16)).slice(-2)
};
  
global.byteArrayToHexString = (byteArray, separator = ' ') => {
    return Array.from(byteArray, byteToHexString).join(separator)
};