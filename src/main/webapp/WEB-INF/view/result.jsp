<%@page contentType = "text/html;charset = UTF-8" language = "java" %>
<%@page isELIgnored = "false" %>
<%@taglib uri = "http://www.springframework.org/tags/form" prefix = "form"%>
<html>
<head>
    <title>Payment Service</title>
</head>

<body>
<h2>Submitted Payment Succeeded</h2>
    <form:form method = "GET" action = "/payment">
        <table>
            <tr>
                <td>Payment: </td>
                <td>${paymentid}</td>
            </tr>
            <tr>
                <td>Payer: </td>
                <td>${payerid}</td>
            </tr>
            <tr>
                <td>Payee: </td>
                <td>${payeeid}</td>
            </tr>
            <tr>
                <td>Currency: </td>
                <td>${currency}</td>
            </tr>
            <tr>
                <td>Amount: </td>
                <td>${amount}</td>
            </tr>
            <tr>
                <td>Payment Method: </td>
                <td>${paymentmethodid}</td>
            </tr>
            <tr>
                <td>Description: </td>
                <td>${paymentdescription}</td>
            </tr>
            <br>
            <br>
            <tr>
                <td colspan = "2">
                    <input type = "submit" value = "Additional Payment"/>
                </td>
            </tr>
        </table>
    </form:form>
</body>

</html>
