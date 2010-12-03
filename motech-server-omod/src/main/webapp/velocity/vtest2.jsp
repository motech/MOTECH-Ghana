<vxml version="2.1">

<var name="screen"/>
<var name="retryN" expr="0"/>
<var name="state" expr="'init'"/>
<var name="userId" expr="'${data.id}'" />
<var name="userName" expr="'${data.userName}'" />

<form id="F2">

<block>
	<prompt>Welcome user <value expr="userName"/> with id <value expr="userId"/> to open m r s main menu which will be developed shortly.</prompt>
</block>

</form>

<error>
  	<if cond="_event =='error.*'">
  		<exit/>
  	</if>
</error>

</vxml>
