package org.motech.event.impl;

import java.util.ArrayList;
import java.util.List;

import org.motech.event.Command;

public class CompositeCommand implements Command {

	List<Command> commands = new ArrayList<Command>();

	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	public void execute() {
		for (Command command : commands) {
			command.execute();
		}
	}
}
