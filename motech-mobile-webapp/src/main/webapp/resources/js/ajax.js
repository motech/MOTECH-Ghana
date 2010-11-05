/*
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

function getXMLHTTP()
{
    //Create a boolean variable to check for a valid Internet Explorer instance.
    var xmlhttp = false;
    var message = '';
    //Check if we are using IE.
    try
    {
        //If the Javascript version is greater than 5.
        xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
    }
    catch (e)
    {
        //If not, then use the older active x object.
        try
        {
            //If we are using Internet Explorer.
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        catch (E)
        {
            //Else we must be using a non-IE browser.
            xmlhttp = false;
        }
    }
    //If we are using a non-IE browser, create a javascript instance of the object.
    if (!xmlhttp && typeof XMLHttpRequest != 'undefined')
    {
        xmlhttp = new XMLHttpRequest();
    }
    return xmlhttp;
}

function makeRequest(serverPage, objID, method, params, loadingHTML)
{
    var obj = document.getElementById(objID);
    obj.innerHTML = loadingHTML;
    xmlhttp = getXMLHTTP();

    if(method == "GET")
    {
        xmlhttp.open("GET", serverPage);
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4){
                if(xmlhttp.status == 200) {
                    obj.innerHTML = '<div style=\'padding-left: 10px; padding-top: 20px;\'>'+xmlhttp.responseText.replace('\n','<br />','g', 'm')+'</div>';
                }
                else
                    obj.innerHTML = '<div style=\'padding-left: 10px; padding-top: 20px; width: 120px;\'>An error occurred! Please try again.</div>';
            }
        }
        xmlhttp.send(null);
    }else{
        xmlhttp.open("POST", serverPage, true);
        xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4){
                if(xmlhttp.status == 200) {
                    obj.innerHTML = '<div style=\'padding-left: 10px; padding-right: 5px; padding-top: 20px;\'>'+xmlhttp.responseText.replace('\n','<br />','g', 'm')+'</div>';
                }
                else
                    obj.innerHTML = '<div style=\'padding-left: 10px; padding-right: 5px; padding-top: 20px; width: 120px;\'>An error occurred! Please try again.</div>';
            }
        }
        xmlhttp.send(params);
    }
}

function getFormValues(fobj)
{
    var str = "";
    var element = "";
    //Run through a list of all objects contained within the form.
    for(var i = 0; i < fobj.elements.length; i++)
    {
        element = fobj.elements[i].name + "=" + escape(fobj.elements[i].value) + "&";
        str += element;
    }
    //Then return the string values.
    return str;
}