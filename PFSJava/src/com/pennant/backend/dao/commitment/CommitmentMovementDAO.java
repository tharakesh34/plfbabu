package com.pennant.backend.dao.commitment;

import com.pennant.backend.model.commitment.CommitmentMovement;

public interface CommitmentMovementDAO {
	public CommitmentMovement getCommitmentMovement();
	public CommitmentMovement getNewCommitmentMovement();
	public CommitmentMovement getCommitmentMovementById(String id,String type);
	public void update(CommitmentMovement commitmentMovement,String type);
	public void delete(CommitmentMovement commitmentMovement,String type);
	public String save(CommitmentMovement commitmentMovement,String type);
	public void initialize(CommitmentMovement commitmentMovement);
	public void refresh(CommitmentMovement entity);
	public int getMaxMovementOrderByRef(String cmtReference);
	public void deleteByRef(String cmtReference, String type);
}
