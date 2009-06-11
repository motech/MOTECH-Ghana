<form action="${pageContext.request.contextPath}/regTest" method="post">
	<label for="nursePhone">Nurse Phone:</label>
	<input name="nursePhone" />
	
	<label for="serialId">Serial Id:</label>
	<input name="serialId" />
	
	<label for="name">Name:</label>
	<input name="name" />
	
	<label for="community">Community:</label>
	<input name="community" />
	
	<label for="location">Location:</label>
	<input name="location" />
	
	<label for="nhis">NHIS:</label>
	<input name="nhis" />
	
	<label for="dueDate">Due Date:</label>
	<input name="dueDate" />
	
	<label for="age">Age:</label>
	<input name="age" />
	
	<label for="parity">Parity:</label>
	<input name="parity" />
	
	<label for="hemoglobin">Hemoglobin:</label>
	<input name="hemoglobin" />
	
	<input type="submit" />

	<!-- 
				String nurseId = req.getParameter("nurseId");
			String serialId = req.getParameter("serialId");
			String name = req.getParameter("name");
			String community = req.getParameter("community");
			String location = req.getParameter("location");

			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date dueDate = dateFormat.parse(req.getParameter("dueDate"));

			Integer age = Integer.valueOf(req.getParameter("age"));
			Integer parity = Integer.valueOf(req.getParameter("parity"));
			Integer hemoglobin = Integer
					.valueOf(req.getParameter("hemoglobin"));
					 -->
</form>