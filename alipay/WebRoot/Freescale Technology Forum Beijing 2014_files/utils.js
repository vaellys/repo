var rp = {};
rp.utils = {};
rp.utils.keys = {}; // obj for handling keys
rp.utils.keys.data = null; // store name/value pairs here

rp.utils.keys.getKeyValue = function (keyName) {
  // returns key name if key not found in string table

  var key = rp.utils.keys.data[keyName];
  if (typeof key === "undefined") { key = '<span class="red">' + keyName + '</span>'; }

  return key;
};


// get url parms
var URLParms;
(window.onpopstate = function () {
  var match,
  pl = /\+/g,  // Regex for replacing addition symbol with a space
  search = /([^&=]+)=?([^&]*)/g,
  decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
  query = window.location.search.substring(1);

  URLParms = {};
  while (match = search.exec(query))
    URLParms[decode(match[1])] = decode(match[2]);
})();

// generic function to handle ajack postback
// url = where to post data and what server side method to call
// myjasondata = data to send to server side method in json format
// callback = array functions to call once server side code is finished executing
function AJAXJSONPost(url, myjsondata, isAsync, callback) {
  var showLoader = setTimeout("$('#div_loading').fadeIn(800)", 2000);
  var ret = null; // data to return

  $.ajax({
    type: 'post',
    url: url,
    contentType: "application/json; charset=utf-8",
    data: JSON.stringify(myjsondata),
    dataType: "json",
    async: isAsync,
    error: function (xhr, ajaxOptions, thrownError) {
      clearTimeout(showLoader);
      $('#div_loading').hide(0);
     // $('#div_Message').show();
      //$('#div_Message').html(thrownError);
      alert("There was a problem processing your request on the server.\nContact support at Results Plus if the problem persists.");
      //alert(thrownError);
    },
    success: function (data) {
      clearTimeout(showLoader);
      $('#div_loading').fadeOut(800);
      ret = data.d;

      // array of callbacks
      if (callback.length > 0) {
        for (var i = 0; i <= callback.length - 1; i++) {
          callback[i](ret);
        }
      }
    }
  });

  return ret;
}