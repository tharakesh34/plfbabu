package com.pennant.backend.dao.commitment;

import com.pennant.backend.model.commitment.CommitmentMovement;

public interface CommitmentMovementDAO {
	
	CommitmentMovement getCommitmentMovement();
	CommitmentMovement getNewCommitmentMovement();
	CommitmentMovement getCommitmentMovementById(String id,String type);
	void update(CommitmentMovement commitmentMovement,String type);
	void delete(CommitmentMovement commitmentMovement,String type);
	String save(CommitmentMovement commitmentMovement,String type);
	int getMaxMovementOrderByRef(String cmtReference);
	void deleteByRef(String cmtReference, String type);
}
