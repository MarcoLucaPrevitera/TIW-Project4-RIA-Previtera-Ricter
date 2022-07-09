{
	let accountsList,accountDetails, transferResult, transferForm,
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
		
		this.show = function(click) {
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
	              if (click) click(); // show the default element of the list if present
	            
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
	        balancecell.textContent = account.balance.toFixed(2)+ " €";
	        row.appendChild(balancecell);
	        linkcell = document.createElement("td");
	        anchor = document.createElement("a");
	        linkcell.appendChild(anchor);
	        linkText = document.createTextNode("Show");
	        anchor.appendChild(linkText);
	        
	        anchor.setAttribute('accountid', account.id); // set a custom HTML attribute
	        anchor.addEventListener("click", (e) => {
	        	accountDetails.show(e.target.getAttribute("accountid"));
	        }, false);
	        anchor.href = "#";
	        row.appendChild(linkcell);
	        self.accountlistbody.appendChild(row);
	      });
	      this.accountlistbody.style.visibility = "visible";

	    }
	    
	    this.autoclick = function(accountId) {
	      var e = new Event("click");
	      var selector = "a[accountid='" + accountId + "']";
	      var anchorToClick = (accountId) ? document.querySelector(selector) : this.accountlistbody.querySelectorAll("a")[0];
	      if (anchorToClick) anchorToClick.dispatchEvent(e);
	    }
	    
	    this.reset = function(){
			
		}
		
	}
	
	function TransferForm(_transferform,_autofilltable){
		this.transferform=_transferform;
		this.autofilltable=_autofilltable;
		
		this.reset = function(){
			
		}
	}

	function TransferResult(params){
		this.resprevbalanceorig = params['resprevbalanceorig'];
		this.resprevbalancedest= params['resprevbalancedest'];
		this.rescurrbalanceorig = params['rescurrbalanceorig'];
		this.rescurrbalanceodest = params['rescurrbalanceodest'];
		this.createNewTransfer = params['createNewTransfer'];
		
		this.reset = function(){
			
		}
	}
	
	function AccountDetails(params){
		this.name = params['name'];
		this.surname = params['surname'];
		this.username = params['username'];
		this.accountnumber = params['accountnumber'];
		this.accountbalance = params['accountbalance'];
		this.transferslist=params['transferslist'];
		this.transfersbody = params['transfersbody'];
		this.emptyalert= params['emptyalert'];
		
		this.show = function(accountId) {
	      var self = this;
	      makeCall("GET", "AccountDetails?accountId="+accountId, null,
	        function(req) {
	          if (req.readyState == XMLHttpRequest.DONE) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var accountDetails = JSON.parse(req.responseText).account;
	              var transfers = JSON.parse(req.responseText).transferList;
	              self.update(accountDetails,transfers); // self visible by closure
	            
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
	    
	    this.update = function(details, transfers){
		    var row,datecell,origcodecell,destcodecell,motivationcell,amountcell;
			var self = this;
			self.name.textContent=sessionStorage.getItem("name");
			self.surname.textContent=sessionStorage.getItem("surname");
			self.username.textContent=sessionStorage.getItem("username");
			self.accountnumber.textContent=details.code;
			self.accountbalance.textContent=details.balance.toFixed(2);
			
			if (transfers.length == 0) {
	                this.transferslist.style.visibility="hidden";
	                this.emptyalert.style.visibility="visible";
	                return;
	              }
			
			this.emptyalert.style.visibility="hidden";
			this.transferslist.style.visibility="visible";
			this.transfersbody.innerHTML="";
			
			transfers.forEach(function(transfer){
			    row = document.createElement("tr");
			    datecell = document.createElement("td");
		        datecell.textContent = transfer.date;
		        row.appendChild(datecell);
		        origcodecell = document.createElement("td");
		        origcodecell.textContent = transfer.accountCodeOrigin;
		        if(transfer.accountCodeOrigin===details.code){
					origcodecell.className="myself";
				}
		        row.appendChild(origcodecell);
		        destcodecell = document.createElement("td");
		        destcodecell.textContent = transfer.accountCodeDest;
		        if(transfer.accountCodeDest===details.code){
					destcodecell.className="myself";
				}
		        row.appendChild(destcodecell);
		        motivationcell = document.createElement("td");
		        motivationcell.textContent = transfer.motivation;
		        row.appendChild(motivationcell);
		        amountcell = document.createElement("td");
		        amountcell.textContent = transfer.amount.toFixed(2) +" €";
		        if(transfer.amount>0){
					amountcell.className="incoming";
				}
				else{
					amountcell.className="outcoming";
				}
		        row.appendChild(amountcell);
		        
		        self.transfersbody.appendChild(row);
			});
		}
	
		this.reset = function(){
				this.emptyalert.style.visibility="hidden";
				this.transferslist.style.visibility="hidden";
		}
	  
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
				 accountbalance:document.getElementById("id_accountbalance"),
				 transferslist: document.getElementById("id_transferslist"),
				 transfersbody: document.getElementById("id_transfersbody"),
				 emptyalert:document.getElementById("id_emptyalert")
				});
			
			document.querySelector("a[href='Logout']").addEventListener('click', () => {
	        window.sessionStorage.removeItem('username');
	        window.sessionStorage.removeItem('name');
	        window.sessionStorage.removeItem('surname');
	      })	
		}
	
	
	this.refresh = function(currentAccount){
		accountsList.show(function() {
	        accountsList.autoclick(currentAccount); 
	      });
	      
	    accountsList.reset();
	    transferForm.reset();
	    transferResult.reset();
	    accountDetails.reset();
	    
	}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}