package com.pennanttech.pff.scheduler.jobs;

import java.util.ArrayList;
import java.util.List;

import org.quartz.JobDataMap;

import com.pennanttech.external.collectionreceipt.job.ExtCollectionFileExtractionJob;
import com.pennanttech.external.collectionreceipt.job.ExtCollectionFileProcessorJob;
import com.pennanttech.external.collectionreceipt.job.ExtCollectionFolderReaderJob;
import com.pennanttech.external.collectionreceipt.job.ExtCollectionResponseFileJob;
import com.pennanttech.external.presentment.job.ExtPresentmentFileExtractionJob;
import com.pennanttech.external.presentment.job.ExtPresentmentFileProcessorJob;
import com.pennanttech.external.presentment.job.ExtPresentmentFolderReaderJob;
import com.pennanttech.external.presentment.job.ExtPresentmentTableReaderJob;
import com.pennanttech.external.silien.job.LienFileReadingJob;
import com.pennanttech.external.silien.job.LienFileWritingJob;
import com.pennanttech.external.silien.job.LienMarkProcessingJob;
import com.pennanttech.external.ucic.service.ExtUcicResponseJob;
import com.pennanttech.external.ucic.service.ExtUcicWeekFileJob;
import com.pennanttech.pennapps.core.job.scheduler.JobData;

public class HDFCJobScheduler implements JobSchedulerExtension {

	@Override
	public List<JobData> loadJobs() {
		List<JobData> jobDataList = new ArrayList<>();

		/**
		 * 29. EXT_PRMNT_RESPONSE_READING_JOB
		 */
		JobDataMap args = new JobDataMap();
		JobData jobData = new JobData("EXT_PRMNT_RESPONSE_READING_JOB", ExtPresentmentFolderReaderJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 30. EXT_PRMNT_FILE_EXTRACTOR_JOB
		 */
		args = new JobDataMap();
		jobData = new JobData("EXT_PRMNT_FILE_EXTRACTOR_JOB", ExtPresentmentFileExtractionJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 34. EXT_PRMNT_FILE_PROCESSOR_JOB
		 */
		args = new JobDataMap();
		jobData = new JobData("EXT_PRMNT_FILE_PROCESSOR_JOB", ExtPresentmentFileProcessorJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 35. EXT_PRMNT_STAGE_TABLE_PROCESSOR_JOB
		 */
		args = new JobDataMap();
		jobData = new JobData("EXT_PRMNT_STAGE_PROCESSOR_JOB", ExtPresentmentTableReaderJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 36. SI_LIEN_MARKING_JOB
		 */
		args = new JobDataMap();
		jobData = new JobData("SI_LIEN_MARKING_JOB", LienMarkProcessingJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 37. SI_LIEN_FILE_WRITING_JOB
		 */
		args = new JobDataMap();
		jobData = new JobData("SI_LIEN_FILE_WRITING_JOB", LienFileWritingJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 38. SI_LIEN_RESPONSE_JOB
		 */
		args = new JobDataMap();
		jobData = new JobData("SI_LIEN_RESPONSE_JOB", LienFileReadingJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 39. EXT_UCIC_WEEKLY_FILE_JOB
		 */
		args = new JobDataMap();
		jobData = new JobData("EXT_UCIC_WEEKLY_FILE_JOB", ExtUcicWeekFileJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 40. EXT_UCIC_RESPONSE_JOB
		 */
		args = new JobDataMap();
		jobData = new JobData("EXT_UCIC_RESPONSE_JOB", ExtUcicResponseJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 41. EXT_COLLECTION_FOLDER_READER_JOB
		 */
		args = new JobDataMap();
		jobData = new JobData("EXT_COLLECTION_FOLDER_READER_JOB", ExtCollectionFolderReaderJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 42. EXT_COLLECTION_FILE_EXTRACTION_JOB
		 */
		args = new JobDataMap();
		jobData = new JobData("EXT_COLLECTION_FILE_EXTRACTION_JOB", ExtCollectionFileExtractionJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 43. EXT_COLLECTION_FILE_PROCESSOR_JOB
		 */
		args = new JobDataMap();
		jobData = new JobData("EXT_COLLECTION_FILE_PROCESSOR_JOB", ExtCollectionFileProcessorJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 44. EXT_COLLECTION_RESPONSE_FILE_JOB
		 */
		args = new JobDataMap();
		jobData = new JobData("EXT_COLLECTION_RESPONSE_FILE_JOB", ExtCollectionResponseFileJob.class, args);
		jobDataList.add(jobData);

		return jobDataList;
	}

}
