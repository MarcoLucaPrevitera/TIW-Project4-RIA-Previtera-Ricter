{
	let accountsList,accountDetails,transfersList, transferResult, transferForm,
	pageOrchestrator = new PageOrchestrator();
	
	  window.addEventListener("load", () => {
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "index.html";
	    } else {
	      pageOrchestrator.start(); // initialize the components
	      pageOrchestrator.refresh();
	    } // display initial content
	  }, false);
	  
	  
	
	function AccountsList(_accountlist, _accountlistbody){
		this.accountlist = _accountlist;
		this.accountlistbody = _accountlistbody;
		
		this.show = function(next) {
	      var self = this;
	      makeCall("GET", "GetAllAccounts", null,
	        function(req) {
	          if (req.readyState == XMLHttpRequest.DONE) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var accountsToShow = JSON.parse(req.responseText);
	              if (accountsToShow.length == 0) {
	                //self.alert.textContent = "No accounts yet!";
	                return;
	              }
	              self.update(accountsToShow); // self visible by closure
	              if (next) next(); // show the default element of the list if present
	            
	          } else if (req.status == 403) {
                  //indow.location.href = req.getResponseHeader("Location");
                  // window.sessionStorage.removeItem('username');
                  }
                  else {
	            //self.alert.textContent = message;
	          }}
	        }
	      );
	    };


	    this.update = function(arrayAccounts) {
	      var row, accountcell, balancecell, linkcell, linkText, anchor;
	      this.accountlistbody.innerHTML = ""; // empty the table body
	      // build updated list
	      var self = this;
	      arrayAccounts.forEach(function(account) { // self visible here, not this
	        row = document.createElement("tr");
	        accountcell = document.createElement("td");
	        accountcell.textContent = account.code;
	        row.appendChild(accountcell);
	        balancecell = document.createElement("td");
	        balancecell.textContent = account.balance;
	        row.appendChild(balancecell);
	        linkcell = document.createElement("td");
	        anchor = document.createElement("a");
	        linkcell.appendChild(anchor);
	        linkText = document.createTextNode("Show");
	        anchor.appendChild(linkText);
	        
	        anchor.setAttribute('accountid', account.id); // set a custom HTML attribute
	        anchor.addEventListener("click", (e) => {
	        //  missionDetails.show(e.target.getAttribute("accountid")); // the list must know the details container
	        }, false);
	        anchor.href = "#";
	        row.appendChild(linkcell);
	        self.accountlistbody.appendChild(row);
	      });
	      this.accountlistbody.style.visibility = "visible";

	    }
		
	}
	
	function TransferForm(_transferform,_autofilltable){
		this.transferform=_transferform;
		this.autofilltable=_autofilltable;
	}

	function TransferResult(params){
		this.resprevbalanceorig = params['resprevbalanceorig'];
		this.resprevbalancedest= params['resprevbalancedest'];
		this.rescurrbalanceorig = params['rescurrbalanceorig'];
		this.rescurrbalanceodest = params['rescurrbalanceodest'];
		this.createNewTransfer = params['createNewTransfer'];
	}
	
	function AccountDetails(params){
	  this.name = params['name'];
	  this.surname = params['surname'];
	  this.ussername = params['username'];
	  this.accountnumber = params['accountnumber'];
	  this.accountbalance = params['accountbalance'];
	}
	
	function TransfersList(_transferslist,_emptyalert){
		this.transferslist=_transferslist;
		this.emptyalert=_emptyalert;
	}
	
	function PageOrchestrator(){
		
		this.start = function() {
			accountsList = new AccountsList(document.getElementById("id_accountslist"),document.getElementById("id_accountslistbody"));
			
			transferForm = new TransferForm(document.getElementById("id_transferform"), document.getElementById("id_autofilltable"));
			
			transferResult = new TransferResult({
				resprevbalanceorig : document.getElementById("id_resprevbalanceorig"),
				resprevbalancedest : document.getElementById("id_resprevbalancedest"),
				rescurrbalanceorig : document.getElementById("id_rescurrbalanceorig"),
				rescurrbalanceodest : document.getElementById("id_rescurrbalancedest"),
				createNewTransfer : document.getElementById("id_createNewTransfer")
			})
			
			accountDetails = new AccountDetails(
				{name:document.getElementById("id_name"),
				 surname:document.getElementById("id_surname"),
				 username:document.getElementById("id_username"),
				 accountnumber:document.getElementById("id_accountnumber"),
				 accountbalance:document.getElementById("id_accountbalance")
				});
			
			transfersList = new TransfersList(document.getElementById("id_transferslist"),document.getElementById("id_emptyalert"));
			
			document.querySelector("a[href='Logout']").addEventListener('click', () => {
	        window.sessionStorage.removeItem('username');
	        window.sessionStorage.removeItem('name');
	        window.sessionStorage.removeItem('surname');
	      })	
		}
	
	
	this.refresh = function(currentAccount){
		accountsList.show();
	}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}