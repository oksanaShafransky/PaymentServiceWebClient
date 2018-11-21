<%@taglib uri = "http://www.springframework.org/tags/form" prefix = "form"%>
<script type="text/javascript" language="JavaScript">
    function filterPaymentMethod(sel){
        var mymethod = "${pageContext.request.getParameterValues("paymentMethodList")}";
        alert("inside filter method " + mymethod );
        var list = $.get("${pageContext.request.contextPath}/filter");
        alert("after filter method " + list.length);
        $('sel').clear;
        $.get("${pageContext.request.contextPath}/filter");
        //$('paymentmethodid').clear;
        //for (var key in list)
        //{
        //    var opt = document.createElement('option');
        //    opt.text = new_options[key];
        //    opt.value = key;
        //    $('paymentmethodid').add(opt, null);
        //}
    }

    function filter() {
        alert("before filter " + '@URL');
        "${pageContext.request.contextPath}/payment/filter";
        var urlInsert = '@Url.Action("filter")';
        $.get(urlInsert, function () {
            alert("after filter");
        });

    }
</script>

<html>
<head>
    <link rel="icon" type="image/png" href="payment.png" />
    <title>Payment Service</title>
    <style type="text/css">
        .error {
            color: red;
        }
    </style>
</head>
<body>

<h2>Welcome to Payment Service</h2>
<form:form method = "POST" action = "/add_payment">
    <table>
            <tr>
                <td><form:label name="payerid" path = "payerid" class="width-50">Payer</form:label></td>
                <td><form:select id="payerid" path="payerid" onchange="filter();">
                    <form:option value="NONE" label="--- Select ---"/>
                    <form:options items="${payerIdList}"/>
                    <form:errors path="payerid" cssClass="error" />
                </form:select>
                </td>
            </tr>
            <tr>
                <td><form:label path = "payeeid" class="width-50">Payee</form:label></td>
                <td><form:select path="payeeid">
                    <form:option value="NONE" label="--- Select ---"/>
                    <form:options items="${payeeIdList}" />
                    <form:errors path="payeeid" cssClass="error" />
                </form:select>
                </td>
            </tr>
        <tr>
            <td><form:label path = "paymentdescription" class="width-50">Description</form:label></td>
            <td><form:input path = "paymentdescription" /></td>
        </tr>
            <tr>
                <td><form:label path = "currency">Currency</form:label></td>
                <td>
                    <form:select path="currency" class="width-50">
                        <form:option value="NONE" label="--- Select ---"/>
                        <form:options items="${currencyList}" />
                    </form:select>
                </td>
            </tr>
            <tr>
                <td><form:label path = "amount">Amount</form:label></td>
                <td><form:input path = "amount" /></td>
                <form:errors path="amount" cssClass="error" />
            </tr>
            <tr>
                <td><form:label path = "paymentmethodid">Payment Method</form:label></td>
                <td><form:select path="paymentmethodid" itemValue="${paymentmethodid}" class="width-50">
                        <form:option value="NONE" label="--- Select ---"/>
                        <form:options items="${paymentMethodList}"/>
                        <form:errors path="paymentmethodid" cssClass="error" />
                    </form:select>
                </td>
            </tr>
            <tr>
                <td><form:label path = "paymentnumber">Payment Number</form:label></td>
                <td>
                    <form:select path="paymentnumber" itemValue="${paymentnumber}" class="width-50">
                        <form:option value="NONE" label="--- Select ---"/>
                        <form:options items="${paymentNumberList}"/>
                    </form:select>
                </td>
            </tr>
            <tr>
                <td colspan = "2">
                    <input type = "submit" value = "Perform Payment"/>
                </td>
            </tr>
        </table>
    </form:form>
</body>
</html>