
(function() { 
  document.getElementById("id_registrationbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    var errorText = document.getElementById("id_registrationerror");
    
    errorText.textContent = "";
    
    var eventfieldset = e.target.closest("fieldset");
	            for (i = 0; i < eventfieldset.elements.length-1; i++) {
	              if (!eventfieldset.elements[i].checkValidity()) {
	                eventfieldset.elements[i].reportValidity();
	                return;
					}
					}
				
				var username = eventfieldset.elements[0].value;
				var password = eventfieldset.elements[3].value;
				var repeatpassword = eventfieldset.elements[4].value;	
				var errorMessage;
				
				var userKeyRegExp = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
                var valid = userKeyRegExp.test(username);
                console.log(valid);
                if(valid===false){
	               errorMessage = "Invalid e-mail";
                }
			
				else if(password!==repeatpassword){
					errorMessage = "The fields password and repeat password do not match."
				}
				
				if(errorMessage){
					errorText.textContent = errorMessage;
					form.reset();
					
				}
				else{
					makeCall("POST", 'Registration', form,
				        function(x) {
				          if (x.readyState == XMLHttpRequest.DONE) {
				            var message = x.responseText;
				            switch (x.status) {
				              case 200:
				                window.location.href = "HomeRIA.html";
				                break;
				              case 400: // bad request
				                errorText.textContent = message;
				                break;
				              case 401: // unauthorized
				                  errorText.textContent = message;
				                  break;
				              case 500: // server error
				            	errorText.textContent = message;
				                break;
				            }
				          }
				        }
				      );
				}
  });
})();