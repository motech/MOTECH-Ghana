<vxml version="2.1">

<var name="screen"/>
<var name="retryN" expr="0"/>
<var name="state" expr="'init'"/>

<form id="F1">

<block>
	<prompt>Welcome to Mo tech, open M R S project</prompt>
</block>

<field name="userId" type="digits?minlength=5;maxlength=18">
			<prompt>Please key in your authentication pin</prompt>

			<filled>
					<prompt>Please wait while we are validating your input.</prompt>
					<assign name="id" expr="userId" />
					<submit next="<%=request.getContextPath()%>/module/motechmodule/vxml2.form" method="get" namelist="id"/>
			</filled>

			<noinput>
			  	<assign name="retryN" expr="retryN + 1"/>
		        <if cond="retryN==3">
		        	<prompt>I did not hear anything. Good bye.</prompt>
		        	<exit/>
		        <else/>
		        	<prompt>I did not hear anything.</prompt>
		        	<reprompt/>
		        </if>
		     </noinput>

		     <nomatch>
		        <assign name="retryN" expr="retryN + 1"/>
		        <if cond="retryN==3">
		        	<prompt>I did not catch that. Please try again next time. Good bye.</prompt>
		        	<exit/>
		        <else/>
		        	<prompt>I did not catch that or you entered the wrong pin which is minimum 5 digits.</prompt>
		        	<reprompt/>
		        </if>
		     </nomatch>


		</field>

</form>


<error>
  	<if cond="_event =='error.*'">
  		<exit/>
  	</if>
</error>

</vxml>
