<%@taglib uri = "http://www.springframework.org/tags/form" prefix = "form"%>
<script type="text/javascript">
    function filterPaymentMethod(){
        var list = filterPaymentMethodList(this);

        $('paymentmethodid').empty();
        for (var key in list)
        {
            var opt = document.createElement('option');
            opt.text = new_options[key];
            opt.value = key;
            $('paymentmethodid').add(opt, null);
        }
    }
</script>
<html>
<head>
    <title>Payment Service</title>
</head>
<body>

<h2>Welcome to Payment Service</h2>
<form:form method = "POST" action = "/add_payment">
    <table>
            <tr>
                <td><form:label path = "payerid" onkeyup="filterPaymentMethod(this)">Payer</form:label></td>
                <td><form:select path="payerid">
                    <form:option value="NONE" label="--- Select ---"/>
                    <form:options items="${payerIdList}" />
                </form:select>
                </td>
            </tr>
            <tr>
                <td><form:label path = "payeeid">Payee</form:label></td>
                <td><form:select path="payeeid">
                    <form:option value="NONE" label="--- Select ---"/>
                    <form:options items="${payeeIdList}" />
                </form:select>
                </td>
            </tr>
        <tr>
            <td><form:label path = "paymentdescription">Description</form:label></td>
            <td><form:input path = "paymentdescription" /></td>
        </tr>
            <tr>
                <td><form:label path = "currency">Currency</form:label></td>
                <td><form:select path="currency">
                    <form:option value="NONE" label="--- Select ---"/>
                    <form:options items="${currencyList}" />
                </form:select>
                </td>
            </tr>
            <tr>
                <td><form:label path = "amount">Amount</form:label></td>
                <td><form:input path = "amount" /></td>
            </tr>
            <tr>
                <td><form:label path = "paymentmethodid">Payment Method</form:label></td>
                <td>
                    <form:select path="paymentmethodid" itemValue="${paymentmethodid}">
                        <form:option value="NONE" label="--- Select ---"/>
                        <form:options items="${paymentMethodList}"/>
                    </form:select>
                </td>
            </tr>
            <br>
            <br>
            <tr>
                <td colspan = "2">
                    <input type = "submit" value = "Perform Payment"/>
                </td>
            </tr>
        </table>
    </form:form>
</body>
</html>