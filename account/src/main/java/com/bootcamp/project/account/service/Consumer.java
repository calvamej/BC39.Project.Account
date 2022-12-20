package com.bootcamp.project.account.service;

import com.bootcamp.project.account.entity.AccountEntity;
import com.bootcamp.project.account.entity.yanki.YankiDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {

	@Autowired
	private MongoTemplate mongoTemplate;

	@KafkaListener(topics="mytopic", groupId="mygroup")
	public void consumeFromTopic(YankiDTO yankiDTO) {

		String response = addYankiOperation(yankiDTO.getDebitCardNumber(), yankiDTO.getType(), yankiDTO.getAmount());
		System.out.println(response);
	}

	public String addYankiOperation(String debitCardNumber, String type, double amount) {

		Query query = new Query();
		query.addCriteria(Criteria.where("debitCardNumber").is(debitCardNumber).and("debitCardMainAccount").is(true));
		AccountEntity entity = mongoTemplate.findOne(query, AccountEntity.class);
		String typePastTense = "";

		if(entity.getAccountNumber() != null)
		{
			Update update = new Update();
			if(type != null && type.toUpperCase().equals("RECEIVE"))
			{
				update.set("balance",entity.getBalance() + amount);
				typePastTense ="received";
			}
			else if(type != null && type.toUpperCase().equals("SEND"))
			{
				if(entity.getBalance() >= amount)
				{
					update.set("balance",entity.getBalance() - amount);
					typePastTense ="sent";
				}
				else{
					return "The main account associated to the debit card does not have enough funds to send $" + amount;
				}

			}
			mongoTemplate.findAndModify(query, update, AccountEntity.class);
			return "The account " + entity.getAccountNumber() + " associated with the debit card " + debitCardNumber + " " + typePastTense + " $" + amount + ".";
		}
		else
		{
			return "The debit card number does not have a main account associated";
		}
	}
}
