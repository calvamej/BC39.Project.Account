package com.bootcamp.project.account.service;

import com.bootcamp.project.account.entity.AccountEntity;
import com.bootcamp.project.account.entity.operation.OperationDTO;
import com.bootcamp.project.account.entity.yanki.YankiDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Consumer {

	@Autowired
	private MongoTemplate mongoTemplate;
	public static final String topic = "mytopicOperation";

	@Autowired
	private KafkaTemplate<String, OperationDTO> kafkaTemp;

	@KafkaListener(topics="mytopic", groupId="mygroup")
	public void consumeFromTopic(YankiDTO yankiDTO) {

		String response = addYankiOperation(yankiDTO.getDebitCardNumber(), yankiDTO.getType(), yankiDTO.getAmount());
		System.out.println(response);
	}
	@Cacheable(value = "accountCache")
	public AccountEntity getAccount(String debitCardNumber, Query query)
	{
		return mongoTemplate.findOne(query, AccountEntity.class);
	}
	public AccountEntity update(Query query,Update update)
	{
		return mongoTemplate.findAndModify(query, update, AccountEntity.class);
	}
	public String addYankiOperation(String debitCardNumber, String type, double amount) {

		Query query = new Query();
		query.addCriteria(Criteria.where("debitCardNumber").is(debitCardNumber).and("debitCardMainAccount").is(true));
		AccountEntity entity = getAccount(debitCardNumber,query);
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
			AccountEntity updatedEntity = update(query, update);
			publishToTopic(entity.getAccountNumber(), typePastTense.toUpperCase() + " - YANKI", amount, entity.getClientDocumentNumber(), entity.getProductCode(), entity.getDebitCardNumber());
			return "The account " + updatedEntity.getAccountNumber() + " associated with the debit card " + updatedEntity.getDebitCardNumber() + " " + typePastTense + " $" + amount + ".";
		}
		else
		{
			return "The debit card number does not have a main account associated";
		}
	}
	public void publishToTopic(String accountNumber, String operationType, Double amount, String clientDocumentNumber, String productCode, String debitCardNumber) {
		OperationDTO operationDTO = new OperationDTO();
		operationDTO.setAccountNumber(accountNumber);
		operationDTO.setOperationType(operationType);
		operationDTO.setAmount(amount);
		operationDTO.setClientDocumentNumber(clientDocumentNumber);
		operationDTO.setProductCode(productCode);
		operationDTO.setDebitCardNumber(debitCardNumber);
		this.kafkaTemp.send(topic, operationDTO);
	}
}
