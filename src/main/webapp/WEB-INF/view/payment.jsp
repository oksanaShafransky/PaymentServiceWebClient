<%@taglib uri = "http://www.springframework.org/tags/form" prefix = "form"%>
<html>
<head>
    <title>Payment Service</title>
    <style type="text/css">
        .error {
            color: red;
        }
    </style>
</head>
<body>

<h1>Welcome to Payment Service</h1>
<form:form method = "POST" action = "/add_payment">
    <table>
        <tr>
            <td><form:label path = "payeeid" class="width-50">Payee</form:label></td>
            <td>
                <form:select path="payeeid" itemValue="${payeeid}" name="payeeid">
                    <form:options items="${payeeid}"/>
                </form:select>
            </td>
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
                <td><form:label path = "paymentnumber">Payment Method</form:label></td>
                <td>
                    <form:select path="paymentnumber" itemValue="${paymentnumber}" name="paymentnumber">
                        <form:options items="${paymentnumber}"/>
                    </form:select>
                </td>
            </tr>
            <tr>
                <td><form:input path = "payerid" value="${payerid}" type="hidden"/></td>
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