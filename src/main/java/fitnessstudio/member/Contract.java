package fitnessstudio.member;

import org.javamoney.moneta.Money;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Contract {

	@Id
	@GeneratedValue
	private long contractId;

	private String name;
	private String description;
	private Money price;

	@OneToMany
	private List<Member> subscriber;

	//duration in days
	private int duration;

	public Contract(){
		subscriber = new ArrayList<>();
	}

	public Contract(String name, String description, Money price, int duration) {
		this();

		this.name = name;
		this.description = description;
		this.price = price;
		this.duration = duration;
	}

	public void update(String name, String description, Money price, int duration){
		this.name = name;
		this.description = description;
		this.price = price;
		this.duration = duration;
	}

	public void subscribe(Member member){
		if (!subscriber.contains(member)){
			subscriber.add(member);
		}
	}

	public long getContractId() {
		return contractId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Money getPrice() {
		return price;
	}

	public int getDuration() {
		return duration;
	}

	public int getNumOfSubs() {return subscriber.size();}
}
