package awsTerm;

import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AccountAttribute;
import com.amazonaws.services.ec2.model.AccountAttributeValue;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DeleteKeyPairResult;
import com.amazonaws.services.ec2.model.DescribeAccountAttributesResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.DryRunResult;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.MonitorInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

public class awsTest {
	
	static AmazonEC2 ec2;
	static ProfileCredentialsProvider credentialsProvider;
	private static void init() throws Exception {
		/* 
		 * The ProfileCredentialsProvider will return your [default]
		 * credential profile by reading from the credentials file located at
		 * (~/.aws/credentials).
		*/
		credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " 
					+"Please make sure that your credentials file is at the correct " 
					+"location (~/.aws/credentials), and is in valid format.",e);
		}
		ec2 = AmazonEC2ClientBuilder.standard()
				.withCredentials(credentialsProvider)
				.withRegion("us-east-1")
				.build();
	}

	public static void main(String[] args) throws Exception {
		init();
		Scanner menu = new Scanner(System.in);
		Scanner id_string= new Scanner(System.in);
		int number = 0;
		boolean flag = true;
		while(flag){
			System.out.println("                                                            ");
			System.out.println("                                                            ");
			System.out.println("------------------------------------------------------------");
			System.out.println("           Amazon AWS Control Panel using SDK               ");
			System.out.println("                                                            ");
			System.out.println("  Cloud Computing, Computer Science Department              ");
			System.out.println("                           at ChungbukNational University  ");
			System.out.println("------------------------------------------------------------");
			System.out.println("  1. list instance                2. available zones        ");
			System.out.println("  3. start instance               4. available regions      ");
			System.out.println("  5. stop instance                6. create instance        ");
			System.out.println("  7. reboot instance              8. list images            ");
			System.out.println("  9. list key pair                10. create key pair       ");
			System.out.println("  11. delete key pair             12. information about the AWS account");
			System.out.println("  13. enable monitoring           99. quit                  ");
			System.out.println("------------------------------------------------------------");
			System.out.print("Enter an integer: ");
			
			number = menu.nextInt();
			
			switch(number) {
				case 1: 
					listInstances();
					break;
				case 2:
					availableZones();
					break;
				case 3:
					startInstance();
					break;
				case 4:
					availableRegions();
					break;
				case 5:
					stopInstance();
					break;
				case 6:
					createInstance();
					break;
				case 7:
					rebootInstance();
					break;
				case 8:
					listImages();
					break;
				case 9:
					listKeyPair();
					break;
				case 10:
					createKeyPair();
					break;
				case 11:
					deleteKeyPair();
					break;
				case 12:
					accountDetail();
					break;
				case 13:
					enableMonitor();
					break;
				case 99:
					flag = false;
			}
		}
	}
	
	/*
	 * 1번 기능
	 */
	public static void listInstances(){
		System.out.println("Listing instances....");
		boolean done = false;
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while(!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);
			for(Reservation reservation: response.getReservations()) {
				for(Instance instance: reservation.getInstances()) {
					System.out.printf("[id] %s, " 
										+"[AMI] %s, " 
										+"[type] %s, " 
										+"[state] %10s, " 
										+"[monitoring state] %s"
										,instance.getInstanceId()
										,instance.getImageId()
										,instance.getInstanceType()
										,instance.getState().getName()
										,instance.getMonitoring().getState());
					}
				System.out.println();
			}
			request.setNextToken(response.getNextToken());
			if(response.getNextToken() == null) {
				done = true;
			}
		}
	}
	
	/*
	 * 2번 기능
	 * 참고 : https://docs.aws.amazon.com/ko_kr/sdk-for-java/v1/developer-guide/examples-ec2-regions-zones.html
	 */
	public static void availableZones() {
		System.out.println("Available regions....");
		
		DescribeAvailabilityZonesResult zonesList = ec2.describeAvailabilityZones();
		
		for(AvailabilityZone zone : zonesList.getAvailabilityZones()) {
		    System.out.printf(
		        "[id] %10s " +
		        "[region] %10s " +
		        "[zone] %10s\n",
		        zone.getZoneId(),
		        zone.getRegionName(),
		        zone.getZoneName());
		}
		
		System.out.println("You have access to " + zonesList.getAvailabilityZones().size() + " Availability Zones.");
	}
	
	/*
	 * 3번 기능
	 * 참고 : https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#Instances:
	 */
	public static void startInstance() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter instance id: ");
		String id = sc.next();
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
		
		StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(id);
		ec2.startInstances(request);
		
		System.out.println("Starting.... " + id + "\n Successfully started instance" + id);
	}
	
	/*
	 * 4번 기능
	 * 참고 : https://docs.aws.amazon.com/ko_kr/sdk-for-java/v1/developer-guide/examples-ec2-regions-zones.html
	 */
	public static void availableRegions() {
		System.out.println("Available regions....");
		DescribeRegionsResult regionsList = ec2.describeRegions();

		for(Region region : regionsList.getRegions()) {
		    System.out.printf(
	    		"[region] %20s, " 
				+"[endpoint] %20s\n" 
				,region.getRegionName()
		       ,region.getEndpoint());
		}
	}
	
	/*
	 * 5번 기능
	 * 참고 : https://docs.aws.amazon.com/ko_kr/sdk-for-java/v1/developer-guide/examples-ec2-instances.html
	 */
	public static void stopInstance() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter instance id: ");
		String id = sc.next();
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(id);
		ec2.stopInstances(request);
		
		System.out.println("Successfully stop instance " + id);
	}
	
	/*
	 * 6번 기능
	 * 참고 : https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#Instances:
	 */
	public static void createInstance() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter ami id: ");
		String id = sc.next();
		RunInstancesRequest run_request = new RunInstancesRequest()
												    .withImageId(id)
												    .withInstanceType(InstanceType.T1Micro)
												    .withMaxCount(1)
												    .withMinCount(1);

		RunInstancesResult run_response = ec2.runInstances(run_request);
		String newId = run_response.getReservation().getInstances().get(0).getInstanceId();
		
		System.out.println("Successfully started EC2 instance " + newId + " based on AMI " + id);
	}
	
	/*
	 * 7번 기능
	 * 참고 : https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#Instances:
	 */
	public static void rebootInstance() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter instance id: ");
		String id = sc.next();

       RebootInstancesRequest request = new RebootInstancesRequest().withInstanceIds(id);
       RebootInstancesResult response = ec2.rebootInstances(request);

		System.out.println("Successfully rebooted instance " + id);
	}
	
	/*
	 * 8번 기능
	 * 트러블슈팅 : https://www.codota.com/code/java/methods/com.amazonaws.services.ec2.AmazonEC2/describeImages
	 */
	public static void listImages() {
		System.out.println("Listing images....");
		DescribeImagesRequest request = new DescribeImagesRequest();
		request.withOwners("self");
		DescribeImagesResult imagesList = ec2.describeImages(request);
		
		for(Image image : imagesList.getImages()) {
		    System.out.printf(
	    		"[imageID] %20s, " 
				+"[Name] %10s, "
				+ "[Owner] %10s \n" 
				,image.getImageId()
		       ,image.getName()
		       ,image.getOwnerId());
		}
	}
	
	/*
	 * 9번 기능
	 * 참고 : https://docs.aws.amazon.com/code-samples/latest/catalog/java-ec2-src-main-java-aws-example-ec2-DescribeKeyPairs.java.html
	 */
	public static void listKeyPair() {
		DescribeKeyPairsResult response = ec2.describeKeyPairs();

		for(KeyPairInfo key_pair : response.getKeyPairs()) {
		    System.out.printf(
		        "[Name] %10s " +
		        "[Fingerprint] %s\n",
		        key_pair.getKeyName(),
		        key_pair.getKeyFingerprint());
		}
	}

	/*
	 * 10번 기능
	 * 참고 : https://docs.aws.amazon.com/code-samples/latest/catalog/java-ec2-src-main-java-aws-example-ec2-CreateKeyPair.java.html
	 */
	public static void createKeyPair() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter key name: ");
		String keyName = sc.next();
		
		CreateKeyPairRequest request = new CreateKeyPairRequest().withKeyName(keyName);
		CreateKeyPairResult response = ec2.createKeyPair(request);
		
		System.out.println("Successfully created key pair " + keyName);
	}

	/*
	 * 11번 기능
	 * 참고 : https://docs.aws.amazon.com/code-samples/latest/catalog/java-ec2-src-main-java-aws-example-ec2-DeleteKeyPair.java.html
	 */
	public static void deleteKeyPair() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter key name: ");
		String keyName = sc.next();
		
		DeleteKeyPairRequest request = new DeleteKeyPairRequest().withKeyName(keyName);
		DeleteKeyPairResult response = ec2.deleteKeyPair(request);
		
		System.out.println("Successfully deleted key pair " + keyName);
	}
	
	/*
	 * 12번 기능
	 * 참고 : https://docs.aws.amazon.com/code-samples/latest/catalog/java-ec2-src-main-java-aws-example-ec2-DescribeAccount.java.html
	 */
	public static void accountDetail() {
		DescribeAccountAttributesResult accountResults = ec2.describeAccountAttributes();
		List<AccountAttribute> accountList = accountResults.getAccountAttributes();
		
		for (ListIterator iter = accountList.listIterator(); iter.hasNext(); ) {
			AccountAttribute attribute = (AccountAttribute) iter.next();
			System.out.print(attribute.getAttributeName());
			List<AccountAttributeValue> values = attribute.getAttributeValues();
			
			for (ListIterator iterVals = values.listIterator(); iterVals.hasNext(); ) {
				AccountAttributeValue AccVal = (AccountAttributeValue) iterVals.next();
				System.out.println(" = " + AccVal.getAttributeValue());
			}
		}	
	}
    
	/*
	 * 13번 기능
	 * 참고 : https://docs.aws.amazon.com/code-samples/latest/catalog/java-ec2-src-main-java-aws-example-ec2-MonitorInstance.java.html
	 */
	@SuppressWarnings("unchecked")
	public static void enableMonitor() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter instnace id: ");
		String id = sc.next();
		
		DryRunSupportedRequest<MonitorInstancesRequest> dryRequest = () -> {
		    MonitorInstancesRequest request = new MonitorInstancesRequest().withInstanceIds(id);		
		    return request.getDryRunRequest();
		};
		DryRunResult dry_response = ec2.dryRun(dryRequest);
		
		MonitorInstancesRequest request = new MonitorInstancesRequest().withInstanceIds(id);
		ec2.monitorInstances(request);
		
		System.out.println("Successfully enabled monitoring");
	}
}
