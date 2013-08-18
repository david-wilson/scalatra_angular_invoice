var app = angular.module('invoice', []);

app.controller('InvoiceCtrl', function($scope, $http, invoiceFactory) {
    $scope.invoice = invoiceFactory.invoice;
    $scope.addRow = invoiceFactory.addRow;
    $scope.deleteRow = invoiceFactory.deleteRow;
    $scope.save = invoiceFactory.save;
    $scope.getSubTotal = invoiceFactory.getSubTotal;
    $scope.getTax = invoiceFactory.getTax;
    $scope.getTotal = invoiceFactory.getTotal;
    $scope.pdfLink ="#";
    $scope.pdfProfessing = false;
    $scope.pdfAvailable = false;

    $scope.pdf = function() {
            console.log("pdf");
            $scope.pdfProfessing = true;
            var html = angular.element("#invoice-page").html();
            var url = "/invoice"
            $http({method: 'POST', url: url, data: html})
                .success(function(data, status, headers, config) {
                    console.log("success");
                    $scope.pdfLink = "/invoice/"+data;
                    $scope.invoice.pdfAvailable = true;
                    $scope.pdfProfessing = false;
                })
                .error(function(data, status, headers, config){
                    console.log("fail");
                    $scope.pdfProfessing = false;
                })
            
        }
});

app.directive('contenteditable', function() {
    var linkFn = function(scope, elm, attrs, ctrl) {
        //view -> model link
        elm.bind('blur', function() {
            scope.$apply(function() {
                ctrl.$setViewValue(elm.html());
            });
        });

        //model -> view link
        ctrl.render = function(value) {
            elm.html(value);
        };

        elm.bind('keydown', function(event) {
            var esc = event.which == 27
            var enter = event.which == 13
            var elem = event.target;

            if (esc || enter) {
                ctrl.$setViewValue(elm.html());
                elem.blur();
                event.preventDefault();
            } 
        });
    }
    return {
        require: 'ngModel',
        link: linkFn,
    }
});

app.factory('invoiceFactory', function() {
    var currentDate = new Date();
    var dateString = currentDate.getDate() + "/" +
                     (currentDate.getMonth()+1) + "/" +
                     currentDate.getFullYear();
    var count = 1;
    var blankRow = {count: count, 
                    item: "Item name (click to change)", 
                    description: "Item description (click to change)",
                    quantity: 0,
                    cost: 0,
                    total: this.quantity * this.cost
            }

    var defaultInvoice = {
        title: "INVOICE",
        company: "Small Business Professionals",
        date: dateString,
        address: "1234 Main St Lakewood, CO 80228",
        phone: "303-555-9999",
        email: "admin@easyinvoicemaker.com",
        number: "1",
        customerName: "Jane Doe",
        customerAddress: "54321 Broadway Street",
        description: "Hello.  Please find a listing of services rendered below.",
        rows: [
            {count: 1, 
             item: "Sprockets", 
             description: "High quality sprockets, delivered",
             quantity: 5,
             cost: 50
            }
        ],
        taxPercent: 7.0,
        note: "Thanks for your business!"
     };

    return {
        invoice: defaultInvoice,
        addRow: function() {
           count++;
           blankRow.count = count;
           this.invoice.rows.push(angular.copy(blankRow));
           this.invoice.pdfAvailable = false;
        },

        deleteRow: function() {
            count--;
            this.invoice.rows.pop();
            this.invoice.pdfAvailable = false;
        },

        save: function() {
            console.log("saved");
        },

        getSubTotal: function() {
           var total = 0;
            angular.forEach(this.invoice.rows, function(row) {
                total += (row.cost*row.quantity);
            });
            return total; 
        },

        getTax: function() {
            return this.getSubTotal()*(this.invoice.taxPercent/100.0);
        },

        getTotal: function() {
            return this.getSubTotal()+this.getTax();
        }

    }
});