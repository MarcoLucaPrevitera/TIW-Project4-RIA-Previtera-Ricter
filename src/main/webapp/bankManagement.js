{
	let accountsList, accountDetails, transferResult, transferForm,
		pageOrchestrator = new PageOrchestrator();

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh();
		}
	}, false);



	function AccountsList(_accountlist, _accountlistbody) {
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
								document.getElementById("id_mainTable").innerHTML ="There are no accounts in your name.";
								return;
							}
							self.update(accountsToShow); // self visible by closure
							if (click) click(); // show the default element of the list if present

						} else if (req.status == 403) {
							logout();
							window.location.href = req.getResponseHeader("Location");
						}
					}
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
				balancecell.textContent = account.balance.toFixed(2) + " €";
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


	}

	function TransferForm(_transferform, _autofilltable) {
		this.transferform = _transferform;
		this.autofilltable = _autofilltable;
		this.formButton = this.transferform.querySelector('input[type="button"]');
		this.addressBook;
		
		this.getAddressBook = function(){
			var self = this;
			
			makeCall("GET", "GetAddressBook", null,
				function(req) {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText;
						if (req.status == 200) {
							self.addressBook = JSON.parse(req.responseText);
							
						} else if (req.status == 403) {
							logout();
							window.location.href = req.getResponseHeader("Location");
						}
					}
				}
			);
		}
		
		
		this.registerListeners = function(){
			var self=this;
			this.formButton.addEventListener("click", (e) => {
		          if (this.transferform.checkValidity()) {
				      makeCall("POST", 'CreateTransfer', e.target.closest("form"),
				        function(x) {
				          if (x.readyState == XMLHttpRequest.DONE) {
				            var message = x.responseText;
				            switch (x.status) {
				              case 200:
				              	var transferDetails = JSON.parse(message);
				              	self.transferform.style.display = "none";
				              	transferResult.showTransferSuccess(transferDetails);
				              	var currentAccount = self.transferform.querySelector("input[type = 'hidden']").value;
				              	accountsList.show(function() {
									accountsList.autoclick(currentAccount);
								});
				                break;
				              case 400: // bad request
				                transferResult.showError(message);
				                break;
				              case 403:
				              	logout();
				              	window.location.href = x.getResponseHeader("Location");
				                break;
				            }
				          }
				        }
				      );
	    		  } 
	    	      else {
	    	 	      this.transferform.reportValidity();
	    		  }  
		 }, false);
		 
		 

		 this.transferform.usernameDest.addEventListener("input", (e) => {
				this.autocomplete(this.transferform.usernameDest.value);
			});

		}
		
		this.autocomplete = function(input){
			var row,usernamecell, codecell, linkcell, linkText, anchor;
			this.autofilltable.style.visibility ="visible";
			this.autofilltable.innerHTML = "";
			if(input.length===0){
				return;
			}
			
			
			var self = this;
			this.addressBook.forEach(function(contact) {
				if(contact.username.substring(0,input.length)===input){
					row = document.createElement("tr");
					usernamecell = document.createElement("td");
					usernamecell.innerHTML="<b>"+input+"</b>"+contact.username.substring(input.length);
					row.appendChild(usernamecell);
					codecell = document.createElement("td");
					codecell.textContent = contact.accountCode;
					row.appendChild(codecell);
					linkcell = document.createElement("td");
					anchor = document.createElement("a");
					linkcell.appendChild(anchor);
					linkText = document.createTextNode("Select");
					anchor.appendChild(linkText);
	
					anchor.setAttribute('username', contact.username); // set a custom HTML attribute
					anchor.setAttribute('code', contact.accountCode); // set a custom HTML attribute
					anchor.addEventListener("click", (e) => {
						self.transferform.usernameDest.value = e.target.getAttribute("username");
						self.transferform.codeAccountDest.value = e.target.getAttribute("code");
						self.autofilltable.innerHTML = "";
					}, false);
					anchor.href = "#";
					row.appendChild(linkcell);
					self.autofilltable.appendChild(row);
				}
			});
			
		}
		
		this.reset = function() {
			this.transferform.style.display = "block";
			this.transferform.style.visibility = "visible";
			this.autofilltable.style.visibility = "hidden";
			this.autofilltable.innerHTML = "";
		}
	}

	function TransferResult(params) {
		this.maincontainer = params['maincontainer'];
		this.transfermessage = params['transfermessage'];
		this.resaccountorig = params['resaccountorig'];
		this.resaccountdest = params['resaccountdest'];
		this.resprevbalanceorig = params['resprevbalanceorig'];
		this.resprevbalancedest = params['resprevbalancedest'];
		this.rescurrbalanceorig = params['rescurrbalanceorig'];
		this.rescurrbalanceodest = params['rescurrbalanceodest'];
		this.createNewTransfer = params['createNewTransfer'];
		this.addForm = params['addForm'];
		this.addButton = params['addButton'];

		this.registerListeners = function(){
			this.createNewTransfer.addEventListener("click", (e) => {
				this.reset();
				transferForm.reset();
			});
			
			this.addButton.addEventListener("click", (e) => {
				makeCall("POST", 'AddAddressBook', this.addForm, function(x){
					if (x.status == 200) {
						transferForm.getAddressBook();
					}
				});
				this.addForm.style.visibility = "hidden";
			});
		}


		this.showTransferSuccess = function(transferData) {
			this.maincontainer.style.visibility = "visible";
			this.transfermessage.style.visibility = "visible";			
			this.transfermessage.textContent = "Transaction successful!"
			this.transfermessage.className = "incoming";
			this.addForm.contactAccountId.value = transferData.account_id_dest;
			this.resaccountorig.textContent = transferData.code_origin;
			this.resaccountdest.textContent = transferData.code_dest;
			this.resprevbalanceorig.textContent = transferData.prev_balance_origin.toFixed(2);
			this.resprevbalancedest.textContent = transferData.prev_balance_dest.toFixed(2);
			this.rescurrbalanceorig.textContent = transferData.curr_balance_origin.toFixed(2);
			this.rescurrbalanceodest.textContent = transferData.curr_balance_dest.toFixed(2);
			var self = this;
			
			this.addForm.style.visibility = "visible";
			transferForm.addressBook.forEach(function(contact) {
				if(contact.accountCode===transferData.code_dest){
					self.addForm.style.visibility = "hidden";
					return;
				}
			});
			
		}
		
		this.showError = function(errorMessage){
			this.transfermessage.style.visibility = "visible";
			this.transfermessage.className = "outcoming";
			this.transfermessage.textContent = errorMessage;
		}

		this.reset = function() {
			this.maincontainer.style.visibility = "hidden";
			this.transfermessage.style.visibility = "hidden";
			this.addForm.style.visibility = "hidden";
		}
	}

	function AccountDetails(params) {
		this.name = params['name'];
		this.surname = params['surname'];
		this.username = params['username'];
		this.accountnumber = params['accountnumber'];
		this.accountbalance = params['accountbalance'];
		this.transferslist = params['transferslist'];
		this.transfersbody = params['transfersbody'];
		this.emptyalert = params['emptyalert'];

		this.show = function(accountId) {
			var self = this;
			makeCall("GET", "AccountDetails?accountId=" + accountId, null,
				function(req) {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText;
						if (req.status == 200) {
							var accountDetails = JSON.parse(req.responseText).account;
							var transfers = JSON.parse(req.responseText).transferList;
							self.update(accountDetails, transfers); // self visible by closure

						} else if (req.status == 403) {
							logout();
							window.location.href = req.getResponseHeader("Location");
						}
					}
				}
			);
		};

		this.update = function(details, transfers) {
			var row, datecell, origcodecell, destcodecell, motivationcell, amountcell;
			var self = this;
			self.name.textContent = sessionStorage.getItem("name");
			self.surname.textContent = sessionStorage.getItem("surname");
			self.username.textContent = sessionStorage.getItem("username");
			self.accountnumber.textContent = details.code;
			self.accountbalance.textContent = details.balance.toFixed(2);
			transferForm.transferform.originAccount.value = details.id;

			if (transfers.length == 0) {
				this.transferslist.style.visibility = "hidden";
				this.transfersbody.innerHTML = "";
				this.emptyalert.style.visibility = "visible";
				return;
			}

			this.emptyalert.style.visibility = "hidden";
			this.transferslist.style.visibility = "visible";
			this.transfersbody.innerHTML = "";

			transfers.forEach(function(transfer) {
				row = document.createElement("tr");
				datecell = document.createElement("td");
				datecell.textContent = transfer.date;
				row.appendChild(datecell);
				origcodecell = document.createElement("td");
				origcodecell.textContent = transfer.accountCodeOrigin;
				if (transfer.accountCodeOrigin === details.code) {
					origcodecell.className = "myself";
				}
				row.appendChild(origcodecell);
				destcodecell = document.createElement("td");
				destcodecell.textContent = transfer.accountCodeDest;
				if (transfer.accountCodeDest === details.code) {
					destcodecell.className = "myself";
				}
				row.appendChild(destcodecell);
				motivationcell = document.createElement("td");
				motivationcell.textContent = transfer.motivation;
				row.appendChild(motivationcell);
				amountcell = document.createElement("td");
				amountcell.textContent = transfer.amount.toFixed(2) + " €";
				if (transfer.amount > 0) {
					amountcell.className = "incoming";
				}
				else {
					amountcell.className = "outcoming";
				}
				row.appendChild(amountcell);

				self.transfersbody.appendChild(row);
			});
		}

		this.reset = function() {
			this.emptyalert.style.visibility = "hidden";
			this.transferslist.style.visibility = "hidden";
		}

	}
	
	function logout(){
		window.sessionStorage.removeItem('username');
		window.sessionStorage.removeItem('name');
		window.sessionStorage.removeItem('surname');
	}

	function PageOrchestrator() {

		this.start = function() {
			accountsList = new AccountsList(document.getElementById("id_accountslist"), document.getElementById("id_accountslistbody"));

			transferForm = new TransferForm(document.getElementById("id_transferform"), document.getElementById("id_autofilltable"));
			transferForm.registerListeners();

			transferResult = new TransferResult({
				maincontainer: document.getElementById("id_transfersuccessful"),
				transfermessage: document.getElementById("id_transferresult"),
				resaccountorig : document.getElementById("id_resaccountorigin"),
				resaccountdest : document.getElementById("id_resaccountdest"),
				resprevbalanceorig: document.getElementById("id_resprevbalanceorig"),
				resprevbalancedest: document.getElementById("id_resprevbalancedest"),
				rescurrbalanceorig: document.getElementById("id_rescurrbalanceorig"),
				rescurrbalanceodest: document.getElementById("id_rescurrbalancedest"),
				createNewTransfer: document.getElementById("id_createNewTransfer"),
				addForm : document.getElementById("id_addressbookform"),
				addButton : document.getElementById("id_addcontactbutton")
			});
			transferResult.registerListeners();

			accountDetails = new AccountDetails(
				{
					name: document.getElementById("id_name"),
					surname: document.getElementById("id_surname"),
					username: document.getElementById("id_username"),
					accountnumber: document.getElementById("id_accountnumber"),
					accountbalance: document.getElementById("id_accountbalance"),
					transferslist: document.getElementById("id_transferslist"),
					transfersbody: document.getElementById("id_transfersbody"),
					emptyalert: document.getElementById("id_emptyalert")
				});

			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				logout();
			});
		}


		this.refresh = function(currentAccount) {
			accountsList.show(function() {
				accountsList.autoclick(currentAccount);
			});
			transferForm.getAddressBook();
			transferForm.reset();
			transferResult.reset();
			accountDetails.reset();

		}
	}

}