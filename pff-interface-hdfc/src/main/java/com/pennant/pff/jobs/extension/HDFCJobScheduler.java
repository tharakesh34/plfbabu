package com.pennant.pff.jobs.extension;

import java.util.ArrayList;
import java.util.List;

import org.quartz.JobDataMap;

import com.pennanttech.external.collectionreceipt.service.ExtCollectionFileExtractionJob;
import com.pennanttech.external.collectionreceipt.service.ExtCollectionFileProcessorJob;
import com.pennanttech.external.collectionreceipt.service.ExtCollectionFolderReaderJob;
import com.pennanttech.external.collectionreceipt.service.ExtCollectionResponseFileJob;
import com.pennanttech.external.presentment.service.ExtPresentmentFileExtractionJob;
import com.pennanttech.external.presentment.service.ExtPresentmentFileProcessorJob;
import com.pennanttech.external.presentment.service.ExtPresentmentFolderReaderJob;
import com.pennanttech.external.presentment.service.ExtPresentmentTableReaderJob;
import com.pennanttech.external.silien.service.LeinFileProcesserJob;
import com.pennanttech.external.silien.service.LienFileReadingJob;
import com.pennanttech.external.silien.service.LienFileWritingJob;
import com.pennanttech.external.ucic.service.ExtUcicResponseJob;
import com.pennanttech.external.ucic.service.ExtUcicWeekFileJob;
import com.pennanttech.pennapps.core.job.scheduler.JobData;
import com.pennanttech.pff.scheduler.jobs.JobSchedulerExtension;

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

		/**
		 * 45. EXT_LIEN_FILE_PROCESSING_JOB
		 */
		args = new JobDataMap();
		jobData = new JobData("SI_LIEN_FILE_PROCESSING_JOB", LeinFileProcesserJob.class, args);
		jobDataList.add(jobData);

		return jobDataList;
	}

}
