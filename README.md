# cloud-music-app
# cloud-music-app
# COSC2626/2640 Assessment 1 – Music Subscription Web Application

## Team leader：
- **Feifan Chen**  
  Student Number: `s3767319`
## Team Members
- **Jessica Bath**  
  Student Number: `s3767319`
- **Christopher Lamb**  
  Student Number: `s3945643`
- **Ze Pan**  
  Student Number: `s3828902`

---

## Overview

This project is part of **Assessment 1** for the COSC2626/2640 Cloud Computing course at RMIT University, Semester 1, 2025.

Our team developed a music subscription web application deployed on an AWS EC2 instance, integrated with **DynamoDB**, **S3**, **API Gateway**, and **Lambda** functions. The application supports user login/registration, music search, subscription, and unsubscription features.

---

## Task 6 – Lambda & API Gateway Integration

For **Task 6**, we successfully replaced the original Spring Boot logic for the **subscribe** and **unsubscribe** functionalities with AWS **Lambda functions** and **API Gateway**.

- When users subscribe or unsubscribe to music tracks, their requests are routed through the API Gateway REST API.
- These requests are then handled by Lambda functions, which interact with the **DynamoDB** `subscriptions` table to store or remove subscription data accordingly.
- This serverless design improves scalability, reduces backend load, and adheres to modern cloud architecture best practices.

---

## Individual Contributions

| Team Member       | Contribution Summary                                                                 |
|-------------------|--------------------------------------------------------------------------------------|
| **Christopher Lamb** | Completed **Task 1**: Creation and setup of DynamoDB login and music tables.       |
| **Ze Pan**           | Completed **Tasks 2, 3, and 4 (backend)**; assisted in **Task 5.3** implementation. |
| **Feifan Chen**      | Completed **Tasks 3 and 4 (frontend)**, **Task 5.1**, **5.2**, **5.4**, and **Task 6**. |
| **Jessica Bath**     | Helped complete **Task 5.3**, especially in music query integration.               |

---



---

## Notes

- All backend and frontend code was developed by the team.
- The application is fully functional and deployed on a public EC2 instance.
- All images are securely retrieved from S3, and all user/music/subscription data is managed through DynamoDB.
