package br.com.it3.model.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "from", "process", "to", "choice" })
public class Route {

	@XmlAttribute
	private String id;

	@XmlElement
	private Address from;
	
	@XmlElement
	public Process process;
	
	@XmlElement
	private Address to;

	@XmlElement
	private Choice choice;
	
	public Route() {
		this.process = new Process();
	}

	public void setTo(Address to) {
		this.to = to;
	}

	public void setFrom(Address from) {
		this.from = from;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setChoice(Choice choice) {
		this.choice = choice;
	}
}
