package com.fetchrewards.backendcodingexercise.controllers;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fetchrewards.backendcodingexercise.models.PointSpend;
import com.fetchrewards.backendcodingexercise.models.Transaction;

@Controller
public class TransactionController {
	List<Transaction> transactions = new ArrayList<>();
	Map<String, Integer> balance = new HashMap<>();
	int totalPointBalance = 0;
	
	@RequestMapping(value = "/transaction/add", method = RequestMethod.GET)
	public String displayTransactionForm(Transaction transaction, Model model) {
		return "transactionForm";
	}
	
	@RequestMapping(value = "/transaction/add", method = RequestMethod.POST)
	public String addTransaction(Transaction transaction, Model model) {
		model.addAttribute("transaction", transaction);
		if(balance.containsKey(transaction.getPayer())) {
			if(balance.get(transaction.getPayer()) + transaction.getPoints() < 0) {
				String negBalMsg = "ERROR: Point balances cannot go negative.";
				model.addAttribute("negBalMsg", negBalMsg);
				return "transactionForm";
			}
		}
		else if(transaction.getPoints() < 0) {
			String negBalMsg = "ERROR: Point balances cannot go negative.";
			model.addAttribute("negBalMsg", negBalMsg);
			return "transactionForm";
		}
		if(transaction.getTimestampString() == null || transaction.getTimestampString() == "") {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			transaction.setTimestamp(timestamp);
		}
		else {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				Date parsedDate = dateFormat.parse(transaction.getTimestampString());
				Timestamp timestamp = new Timestamp(parsedDate.getTime());
				transaction.setTimestamp(timestamp);
			}
			catch(Exception e) {
				String tsFormMsg = "ERROR: Timestamp was entered incorrectly.";
				model.addAttribute("tsFormMsg", tsFormMsg);
				return "transactionForm";
			}
		}
		transactions.add(transaction);
		balance.put(transaction.getPayer(), balance.getOrDefault(transaction.getPayer(), 0) + transaction.getPoints());
		totalPointBalance += transaction.getPoints();
		return "redirect:/balance";
	}
	
	@RequestMapping(value = "/points/spend", method = RequestMethod.GET)
	public String displaySpendForm(PointSpend pointSpend, Model model) {
		return "spendPointsForm";
	}
	
	@RequestMapping(value = "/points/spend", method = RequestMethod.POST)
	public String spendPoints(PointSpend pointSpend, Model model) {
		model.addAttribute("pointSpend", pointSpend);
		if(pointSpend.getPoints() <= totalPointBalance) {
			Map<String, Integer> spentPoints = findOldestPoints(balance, pointSpend.getPoints());
			model.addAttribute("spentPoints", spentPoints);
			totalPointBalance -= pointSpend.getPoints();
			return "spendPointsForm";
		}
		else {
			// Display error message to inform user that they do not have enough points
			String errorMsg = "Sorry! You do not have enough points for that.";
			model.addAttribute("errorMsg", errorMsg);
			return "spendPointsForm";
		}
	}
	
	
	@RequestMapping(value = "/balance", method = RequestMethod.GET)
	public String displayPointBalance(Model model) {
		model.addAttribute("balance", balance);
		return "balance";
	}
	
	public Map<String, Integer> findOldestPoints(Map<String, Integer> map, int target) {
		Map<String, Integer> result = new HashMap<>();
		// Sort the transactions log by their timestamps (oldest comes first)
		transactions.sort((t1, t2) -> t1.getTimestamp().compareTo(t2.getTimestamp()));
		for(Transaction t : transactions) {
			while(target > 0 && t.getPoints() != 0) {
				if(t.getPoints() < 0) {
					if(result.containsKey(t.getPayer())) {
						int posPoints = Math.abs(t.getPoints());
						result.put(t.getPayer(), result.get(t.getPayer()) + t.getPoints());
						balance.put(t.getPayer(), balance.get(t.getPayer()) + posPoints);
						target += posPoints;
						t.setPoints(0);
					}
				}
				else if(t.getPoints() < target) {
					target -= t.getPoints();
					result.put(t.getPayer(), result.getOrDefault(t.getPayer(), 0) + t.getPoints());
					balance.put(t.getPayer(), balance.getOrDefault(t.getPayer(), 0) - t.getPoints());
					t.setPoints(0);
				}
				else if(t.getPoints() > target) {
					result.put(t.getPayer(), result.getOrDefault(t.getPayer(), 0) + target);
					balance.put(t.getPayer(), balance.getOrDefault(t.getPayer(), 0) - target);
					t.setPoints(t.getPoints() - target);
					target = 0;
				}
				else { 
					// t's points == target
					result.put(t.getPayer(), result.getOrDefault(t.getPayer(), 0) + target);
					balance.put(t.getPayer(), balance.getOrDefault(t.getPayer(), 0) - target);
					t.setPoints(0);
					target = 0;
				}
			}
		}
		return result;
	}
}