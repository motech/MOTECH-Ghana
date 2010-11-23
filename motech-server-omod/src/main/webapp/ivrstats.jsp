<%--

    MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT

    Copyright (c) 2010 The Trustees of Columbia University in the City of
    New York and Grameen Foundation USA.  All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

    1. Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

    3. Neither the name of Grameen Foundation USA, Columbia University, or
    their respective contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
    AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
    BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
    FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
    USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
    LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
    OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
    NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
    EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

--%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>IVR Call Statistics</title>
</head>
<body>
<h1>Search</h1>
<table>
<tr>
<form>
<td>
User ID:<input type="text" name="userid"/>
<input type="submit"/>
</td>
</form>
<form>
<td>
Phone Number:<input type="text" name="phone"/>
<input type="submit"/>
</td>
</form>
</tr>
</table>
<h1>Call Session Stats</h1>
<table border="1">
<tr>
<th>Created in:</th>
<th>Last 5 Minutes</th>
<th>Last Hour</th>
<th>Last Day</th>
<th>All</th>
</tr>
<tr>
<td></td>
<td>
<a href="ivrstats?stime=m">${fiveMinuteSessionCount}</a>
</td>
<td>
<a href="ivrstats?stime=h">${oneHourSessionCount}</a>
</td>
<td>
<a href="ivrstats?stime=d">${oneDaySessionCount}</a>
</td>
<td>${allSessionCount}</td>
</tr>
</table>

<h1>Call Stats</h1>


<table>
<tr>

<td>
<table border="1">
<th colspan="2">Last 5 Minutes</th>
<c:forEach var="stat" items="${fiveMinuteCallStats}">
<tr>
<td>${stat.status}</td>
<td>${stat.count}</td>
</tr>
</c:forEach>
</table>
</td>

<td>
<table border="1">
<th colspan="2">Last Hour</th>
<c:forEach var="stat" items="${oneHourCallStats}">
<tr>
<td>${stat.status}</td>
<td>${stat.count}</td>
</tr>
</c:forEach>
</table>
</td>

<td>
<table border="1">
<th colspan="2">Last Day</th>
<c:forEach var="stat" items="${oneDayCallStats}">
<tr>
<td>${stat.status}</td>
<td>${stat.count}</td>
</tr>
</c:forEach>
</table>
</td>

<td>
<table border="1">
<th colspan="2">All</th>
<c:forEach var="stat" items="${allCallStats}">
<tr>
<td>${stat.status}</td>
<td>${stat.count}</td>
</tr>
</c:forEach>
</table>
</td>

</tr>
</table>

<h1>Recording Stats</h1>

<table border="1">
<th>Name</th>
<th>Listens</th>
<th>Average Time Listened</th>
<c:forEach var="stat" items="${recordingStats}">
<tr>
<td>${stat.name}</td>
<td>${stat.totalListens }</td>
<td>${stat.averageTimeListened }</td>
</tr>
</c:forEach>

</table>

</body>
</html>