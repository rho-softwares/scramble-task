// FINAL SIGNATURE FOR: [2] - A FUNCTIONAL MODULE (To some other script/JSfile require and use this)
async function function1(inputPromise) {
    let responseMap = {data: [], errors: []}

    async function myAscyncAwait(inputPromise) {
      try         { responseMap['data'] = await inputPromise; }
      catch(err)  { console.log("[ERROR] TRY/CATCH + ASYNC/AWAIT Pegou erro do AXIOS-JSON-FETCH");
                      responseMap['errors'] = [err];}
      finally      { return responseMap; }}
    let result = await myAscyncAwait(inputPromise);
    //console.log("responseMap: ", result);
    return result;};

module.exports = function1;