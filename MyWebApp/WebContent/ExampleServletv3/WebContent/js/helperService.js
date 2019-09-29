/*
* Helper service which takes a query and matching text and highlights the matching parts.
* This helper makes use of the Angular $sce (Strict Contextual Escaping) object which allows
* to add inner HTML into other HTML elements. 
* See more details in: https://docs.angularjs.org/api/ng/service/$sce
*/
angular.module('txtHighlight',[])
  .factory('highlightText', ['$sce', function($sce){
        
		//service method to be called upon text highlighting
		var highlight = function(text, qstr){
		
		    if (!qstr || qstr.length == 0) return $sce.trustAsHtml(text);
		    
			var lcqstr = qstr.toLowerCase();
			var lctext = text.toLowerCase();
			var i = lctext.indexOf(lcqstr);
			
			if (i >= 0){
			    
				var prfxStr = text.substring(0,i);
				var match = text.substring(i,i + qstr.length);
				var sfxStr = text.substring(i + qstr.length, text.length);
				//we wrap the matching text with <mark>...</mark> (bootstrap element) that highlights the matching string
				var hgltText = prfxStr + '<mark>' + match + '<\/mark>' + sfxStr;
				return $sce.trustAsHtml(hgltText);
			}else{
				return $sce.trustAsHtml(text);
			}
	  };
	  
	  return {
		highlight: highlight
	  };
	  
  }]);