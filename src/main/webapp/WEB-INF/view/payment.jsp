<%@taglib uri = "http://www.springframework.org/tags/form" prefix = "form"%>
<script type="text/javascript" language="JavaScript">
    function filterPaymentMethod(){
        var list = filterPaymentMethodList(this);
        var paymentmethodstate = new DynamicOptionList();
        paymentmethodstate.addDependentFields("payerid","paymenmethodid");
        paymentmethodstate.addOptions("California","Washington","Oregon");

        alert("inside filter method " + this);

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
                <td><form:label path = "payerid" class="width-50">Payer</form:label></td>
                <td><form:select path="payerid">
                    <form:option value="NONE" label="--- Select ---"/>
                    <form:options items="${payerIdList}" />
                        <fmt:message key="cf_gender.${item.getCfGender()}" />
                    <font color="red"><form:errors path="payerid"></form:errors></font>
                </form:select>
                </td>
            </tr>
            <tr>
                <td><form:label path = "payeeid" class="width-50">Payee</form:label></td>
                <td><form:select path="payeeid">
                    <form:option value="NONE" label="--- Select ---"/>
                    <form:options items="${payeeIdList}" />
                    <font color="red"><form:errors path="payeeid"></form:errors></font>
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
                <font color="red"><form:errors path="amount"></form:errors></font>
            </tr>
            <tr>
                <td><form:label path = "paymentmethodid">Payment Method</form:label></td>
                <td>
                    <form:select path="paymentmethodid" itemValue="${paymentmethodid}" class="width-50">
                        <form:option value="NONE" label="--- Select ---"/>
                        <form:options items="${paymentMethodList}"/>
                        <font color="red"><form:errors path="paymentmethodid"></form:errors></font>
                    </form:select>
                </td>
            </tr>
            <tr>
                <td><form:label path = "paymentnumber">Payment Number</form:label></td>
                <td>
                    <form:select path="paymentnumber" itemValue="${paymentnumber}" class="width-50">
                        <form:option value="NONE" label="--- Select ---"/>
                        <form:options items="${paymentNumberList}"/>
                        <font color="red"><form:errors path="paymentnumber"></form:errors></font>
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