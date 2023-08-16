void COptionDialog::ch_singlewt(CString filename, double freqresolution, double freqstart, double freqend, double timeresolution, double timestart, double timeend, char outputtype, int waveletsize, BOOL decon, int autotime, int autofreq)
{
		

		int scales;
		int datasize;
		int nIndex;
		int channel;
		int fromPos;
		int toPos;
		double timeofrecord;
		double dummy;
		double scalevalue = 0;
		double maxamplitude = 0;
		CString fileout;
		CString filetype;
		CString units;
		CString date;
		CString ctime;

		CString value;

		static dcmplx signal[MAXDATASIZE];
		static double signalinv[MAXDATASIZE];
		static dcmplx coeffrow[MAXDATASIZE];
		static dcmplx coeffmatrix[MAXSCALESIZE][MAXDATASIZE];

		//get filetype (.txt) expected
		nIndex = filename.ReverseFind('.');
		filetype = filename.Right(filename.GetLength()-nIndex);
		filetype.MakeLower();

		//check consistence of parameters ...
		if(filetype == ".txt")
		{
			//load data file into signal[]
			if(autotime == 1)
			{datasize = io_readAEwinFile(filename, signal, channel, timeofrecord, timeresolution, units, date, ctime);}
			else
			{datasize = io_readAEwinFile(filename, signal, channel, timeofrecord, dummy, units, date, ctime);}
			if(datasize <= 0)
			{AfxMessageBox("No valid data present in file or error during loading!");
			return;}

			//create output file
			fileout = filename.Left(nIndex)+"_wt"+filetype;
		}
		else //wrong file format
		{
			AfxMessageBox("Filetype has the wrong format! Provide a valid file!");
			return;
		}

		//clear arrays
		for(int i=0;i<MAXDATASIZE;i++)
		{
			for(int j=0;j<MAXSCALESIZE;j++)
			{coeffmatrix[j][i]= 0;}
		}

		//calculate data sample size for WT-calculation
		fromPos = timestart/timeresolution;
		toPos = timeend/timeresolution;

		//resize array content
		for(int j=0; j<MAXDATASIZE; j++)
		{
			signal[j] = signal[j+fromPos];

			if(j>=(toPos+fromPos))
			{
			signal[j] = NULL;
			}

		}


		

		if(autofreq == 1)
		{
		//interpolate to 100 values
		scales = 100;
		freqresolution = (freqend-freqstart)/scales;
		}
		else
		{
		//calculate hard number of scale steps
		scales = (freqend-freqstart)/freqresolution;
		scales++;
		}
		//value.Format("%d", scales);
		//AfxMessageBox(value);
		
		m_calcprogress.SetRange(0,scales);
		

		//start calculation and pass params

		for(i=1;i<scales;i++)
		{		
			
			//calculate scale params
			scalevalue = pow(timeresolution*(i*freqresolution), -1)*pow(2, 0.5);
			//scale with 1/sqrt(2) !!!

			//information output (progress)
			m_calcprogress.SetPos(i);
			//calculation


			calc_wt(scalevalue, timeresolution, signal, coeffrow, (toPos-fromPos), waveletsize);
			
			//save coefficient row
			dh_storeinmatrix(&coeffmatrix[0][0], coeffrow, i-1, MAXDATASIZE);
			

			//check if dialog was closed
			if(stop_CALC)			
			return;
		
			 //Invalidate(TRUE);
			 while(PeekMessage(&msg,NULL,0,0,PM_REMOVE))
			 {
				TranslateMessage(&msg);
				DispatchMessage(&msg);
			 }

		}
/*	
//TEMP------------------------------------------------------------------------
		for(i=1;i<scales;i++)
		{		
			
			//calculate scale params
			scalevalue = pow(timeresolution*(i*freqresolution), -1)*pow(2, 0.5);
			//scale with 1/sqrt(2) !!!

			//information output (progress)
			//m_calcprogress.SetPos(i);
			//calculation

			calc_wtinv(scalevalue, timeresolution, &coeffmatrix[0][0], signalinv, datasize, waveletsize, i);
			

			//check if dialog was closed
			if(stop_CALC)			
			return;
		
			 //Invalidate(TRUE);
			 while(PeekMessage(&msg,NULL,0,0,PM_REMOVE))
			 {
				TranslateMessage(&msg);
				DispatchMessage(&msg);
			 }

		}	
		io_saveAEwinFile(fileout+"tmp.txt", signalinv, datasize, channel, timeofrecord, timeresolution, units, date, ctime);


//TEMP------------------------------------------------------------------------
*/
	//display result
	IplImage* wt_image = cvCreateImage(cvSize((toPos-fromPos)+1, scales), 8, 3);
	IplImage* signal_image = cvCreateImage(cvSize((toPos-fromPos)+1, 100), 8, 3);
	IplImage* result_image1 = cvCreateImage(cvSize(800, 600), 8, 3);
	IplImage* result_image2 = cvCreateImage(cvSize(800, 100), 8, 3);
	IplImage* combined_image = cvCreateImage(cvSize(800, 700), 8, 3);

	CvFont font;
	cvInitFont(&font, CV_FONT_HERSHEY_SIMPLEX, 0.4, 0.4, 0, 1, 8 );

	dh_createWTFile(wt_image, &coeffmatrix[0][0], (toPos-fromPos)+1, scales, maxamplitude);
        
	//combine to 800x600 pixels
	cvConvertImage( wt_image, wt_image, CV_CVTIMG_FLIP);
	cvResize(wt_image, result_image1, CV_INTER_CUBIC);
	
	//add axis and values
	//horizontal
	cvLine(result_image1, cvPoint(0,596), cvPoint(800,596), cvScalar(255,255,255), 1);
	for(int label=0;label<800;label+=20)
	{cvLine(result_image1, cvPoint(label,598), cvPoint(label,594), cvScalar(255,255,255), 1);}	
	double diversion = ((toPos-fromPos)+1)*timeresolution/40;
	value.Format("%.2e", diversion);
	CString str_time = "time["+value+"s/DIV]";
	cvPutText( result_image1, str_time, cvPoint(650,590), &font,  cvScalar(255,255,255) );

	//vertical
	cvLine(result_image1, cvPoint(4,0), cvPoint(4,600), cvScalar(255,255,255), 1);
	for(label=0;label<600;label+=20)
	{cvLine(result_image1, cvPoint(2,label), cvPoint(6,label), cvScalar(255,255,255), 1);}
	diversion = scales*freqresolution/30;
	value.Format("%.2e", diversion);
	CString str_freq = "frequency["+value+"Hz/DIV]";
	cvPutText( result_image1, str_freq, cvPoint(20,20), &font,  cvScalar(255,255,255) );

	//color-bar with wt-scale
	CvScalar s;
	CString str_coeff;
	//header
	cvPutText( result_image1, "WT-coefficients", cvPoint(695,15), &font,  cvScalar(255,255,255) );
	//frame
	cvRectangle(result_image1, cvPoint(699,19), cvPoint(796,284), cvScalar(255,255,255),-1, 8, 0 );
	//bar
	for(int grayval=0;grayval<256;grayval++)
	{
		
		if(grayval<=128)
				{
					s.val[0] = 0;
					s.val[1] = grayval*2-1;
					s.val[2] = 256-grayval*2;
				}
		if(grayval>128)
				{
					s.val[0] = grayval*2-255;
					s.val[1] = 510-grayval*2;
					s.val[2] = 0;
				}
		cvLine(result_image1, cvPoint(700,grayval+22), cvPoint(720,grayval+22), s, 1);

		//label
		if(grayval%50==0)
		{
			diversion = grayval*maxamplitude/255;
			str_coeff.Format("%.2e", diversion);
			cvPutText( result_image1, str_coeff, cvPoint(724,256-grayval+23), &font,  cvScalar(0,0,0) );

		}
	}

	//now signal image
	dh_createAEwinFile(signal_image, signal, datasize, channel, timeofrecord, timeresolution, units, date, ctime, maxamplitude);
        
	//combine to 800x200 pixels
	cvResize(signal_image, result_image2, CV_INTER_AREA );

	//add axis and values
	//horizontal
	cvLine(result_image2, cvPoint(0,50), cvPoint(800,50), cvScalar(100,100,100), 1);
	for(label=0;label<800;label+=20)
	{cvLine(result_image2, cvPoint(label,54), cvPoint(label,46), cvScalar(100,100,100), 1);}
	cvPutText( result_image2, str_time, cvPoint(650,90), &font,  cvScalar(100,100,100) );

	//vertical
	cvLine(result_image2, cvPoint(4,0), cvPoint(4,200), cvScalar(100,100,100), 1);
	for(label=0;label<200;label+=20)
	{cvLine(result_image2, cvPoint(2,label), cvPoint(6,label), cvScalar(100,100,100), 1);}
	diversion = maxamplitude/5;
	value.Format("%.2e", diversion);
	units.TrimLeft();
	CString str_amp = "amplitude["+value+" "+units+"/DIV]";
	cvPutText( result_image2, str_amp, cvPoint(20,20), &font,  cvScalar(100,100,100) );

	cvSetImageROI(combined_image, cvRect(0, 0, 800, 600));
    cvResize(result_image1, combined_image, CV_INTER_CUBIC );
    cvResetImageROI(combined_image);

	cvSetImageROI(combined_image, cvRect(0, 600, 800, 100));
	cvResize(result_image2, combined_image, CV_INTER_CUBIC );
	cvResetImageROI(combined_image);



	cvNamedWindow( "WT-File", CV_WINDOW_AUTOSIZE);
	cvShowImage( "WT-File", combined_image );
	cvWaitKey(0); // very important, contains event processing loop inside
    cvDestroyWindow( "WT-File" );

	CString graphicfilename;
	graphicfilename = filename.Left(nIndex)+"_wt_preview.bmp";
	cvSaveImage( graphicfilename, combined_image);

	cvReleaseImage( &result_image1 );
	cvReleaseImage( &result_image2 );
	cvReleaseImage( &combined_image );
	cvReleaseImage( &wt_image );
	cvReleaseImage( &signal_image );


/*
	//(3) display file
	IplImage* signal_image = cvCreateImage(cvSize(datasize, 200), 8, 3);
	IplImage* result_image = cvCreateImage(cvSize(800, 200), 8, 3);
	CvFont font;
	cvInitFont(&font, CV_FONT_HERSHEY_SIMPLEX, 0.4, 0.4, 0, 1, 8 );

	dh_createAEwinFile(signal_image, signal, datasize, channel, timeofrecord, timeresolution, units, date, ctime, maxamplitude);
        
	//combine to 800x200 pixels
	cvResize(signal_image, result_image, CV_INTER_AREA );

	//add axis and values
	//horizontal
	cvLine(result_image, cvPoint(0,100), cvPoint(800,100), cvScalar(100,100,100), 1);
	for(int label=0;label<800;label+=20)
	{cvLine(result_image, cvPoint(label,104), cvPoint(label,96), cvScalar(100,100,100), 1);}
	double diversion = datasize*timeresolution/40;
	value.Format("%.2e", diversion);
	CString str_time = "time["+value+"s/DIV]";
	cvPutText( result_image, str_time, cvPoint(650,90), &font,  cvScalar(100,100,100) );

	//vertical
	cvLine(result_image, cvPoint(50,0), cvPoint(50,200), cvScalar(100,100,100), 1);
	for(label=0;label<200;label+=20)
	{cvLine(result_image, cvPoint(46,label), cvPoint(54,label), cvScalar(100,100,100), 1);}
	diversion = maxamplitude/5;
	value.Format("%.2e", diversion);
	CString str_amp = "amplitude["+value+" "+units+"/DIV]";
	cvPutText( result_image, str_amp, cvPoint(55,20), &font,  cvScalar(100,100,100) );



	cvNamedWindow( "AEwin File", CV_WINDOW_AUTOSIZE);
	cvShowImage( "AEwin File", result_image );
	cvWaitKey(0); // very important, contains event processing loop inside
    cvDestroyWindow( "AEwin File" );

	cvReleaseImage( &result_image );
	cvReleaseImage( &signal_image );

*/	
/*
		//create picture from coeffmatrix	
		
		IplImage* WT_image = cvCreateImage(cvSize((toPos-fromPos)+1, scales), 8, 3);
		IplImage* signal_image = cvCreateImage(cvSize((toPos-fromPos)+1, 100), 8, 3);
		IplImage* result_image = cvCreateImage(cvSize(800, 600), 8, 3);



		//capsulate
		dh_createWTimage(WT_image, &coeffmatrix[0][0], scales, (toPos-fromPos), freqresolution, timestart, channel, timeofrecord, timeresolution);
		dh_createSignalimage(signal_image, signal, datasize, channel, timeofrecord, timeresolution, units, date, ctime);

		cvConvertImage( WT_image, WT_image, CV_CVTIMG_FLIP);
        
		//combine to 800x600 pixels
		cvSetImageROI(result_image, cvRect(0, 0, 800, 500));
        cvResize(WT_image, result_image, CV_INTER_CUBIC );
        cvResetImageROI(result_image);

		cvSetImageROI(result_image, cvRect(0, 500, 800, 100));
		cvResize(signal_image, result_image, CV_INTER_CUBIC );
		cvResetImageROI(result_image);


		cvNamedWindow( "Result of WT-Calculation", CV_WINDOW_AUTOSIZE);
		cvShowImage( "Result of WT-Calculation", result_image );
		cvWaitKey(0); // very important, contains event processing loop inside
        cvDestroyWindow( "Result of WT-Calculation" );

		cvNamedWindow( "2", CV_WINDOW_AUTOSIZE);
		cvShowImage( "2", signal_image );
		cvWaitKey(0); // very important, contains event processing loop inside
        cvDestroyWindow( "2" );



        cvReleaseImage( &WT_image );
		cvReleaseImage( &result_image );
		cvReleaseImage( &signal_image );

*/


		if(outputtype == 'c' || outputtype == 'C')
		{
			//save complex coefficients to file
			io_saveCoeffFileComplex(fileout, &coeffmatrix[0][0], scales, (toPos-fromPos), freqresolution, timestart, channel, timeofrecord, timeresolution);
		}

		if(outputtype == 'a' || outputtype == 'A')
		{
			//save Absolute Value  of coefficients in file
			io_saveCoeffFileAbs(fileout, &coeffmatrix[0][0], scales, (toPos-fromPos), freqresolution, timestart, channel, timeofrecord, timeresolution);
		}

}