package com.researchintelligence.config;

import com.researchintelligence.model.ResearchPaper;
import com.researchintelligence.model.User;
import com.researchintelligence.repository.PaperRepository;
import com.researchintelligence.repository.UserRepository;
import com.researchintelligence.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/login", "/register", "/css/**", "/js/**", "/images/**", "/error");
    }

    /**
     * Seeds initial database records if running on H2 or fresh MySQL instance.
     */
    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PaperRepository paperRepository, UserService userService) {
        return args -> {
            if (userRepository.count() == 0) {
                // Seed Admin and Standard User
                userService.registerUser("Dr. Alan Turing", "admin@researchintel.com", "admin123", "ADMIN");
                userService.registerUser("Sarah Jenkins", "user@researchintel.com", "user123", "USER");
            }

            if (paperRepository.count() == 0) {
                paperRepository.save(new ResearchPaper(
                        "Deep Learning Approaches for IoT Security Threat Detection in Smart Homes",
                        "Dr. Elena Rostova, Marcus Vance, Chen Wei",
                        "Internet of Things (IoT) devices in smart homes generate vast volumes of telemetry data that are susceptible to cyber attacks. This paper proposes a lightweight intrusion detection framework utilizing deep neural networks to monitor network packet flow. By analyzing anomaly signatures in real-time edge computing nodes, the proposed framework identifies unauthorized device connections, denial-of-service (DoS) attempts, and malicious firmware manipulation with minimal computational overhead.",
                        "IoT Security, Threat Detection, Smart Home, Deep Learning, Anomaly Detection",
                        2024,
                        "Cybersecurity",
                        "https://doi.org/10.1016/j.iotsec.2024.102341"
                ));

                paperRepository.save(new ResearchPaper(
                        "Hybrid Machine Learning Models for Network Intrusion Detection Systems",
                        "Prof. David K. Miller, Sophia Martinez",
                        "Network Intrusion Detection Systems (NIDS) are critical defensive infrastructure for modern enterprise networks. Traditional signature-based detection mechanisms fail to identify zero-day exploits. We present a hybrid classification framework combining Random Forest and Gradient Boosted Decision Trees to analyze high-dimensional network traffic logs. Experimental evaluation on standard benchmark datasets demonstrates superior precision, low false-positive rates, and resilience against adversarial evasion tactics.",
                        "Network Intrusion Detection, NIDS, Machine Learning, Cyber Attack, Zero-Day Exploits",
                        2023,
                        "Network Security",
                        "https://doi.org/10.1109/TNSM.2023.3289104"
                ));

                paperRepository.save(new ResearchPaper(
                        "Secure Communication Protocols for Constrained IoT Edge Nodes",
                        "Rajesh Kumar, Anita Patel, Dr. John Harris",
                        "Resource-constrained Internet of Things nodes often lack hardware acceleration for complex cryptographic algorithms. This paper introduces an energy-efficient secure communication protocol leveraging lightweight symmetric key encryption and dynamic key rotation. The protocol minimizes handshake latency and packet overhead while protecting data confidentiality and device authentication across wireless sensor networks.",
                        "IoT Communication, Lightweight Cryptography, Wireless Sensor Networks, Encryption, Device Authentication",
                        2025,
                        "IoT Security",
                        "https://doi.org/10.1016/j.comnet.2025.109882"
                ));

                paperRepository.save(new ResearchPaper(
                        "Zero-Trust Cloud Security Architecture: Frameworks and Real-World Evaluation",
                        "Amanda Zhang, Robert Sterling, Michael Chang",
                        "Cloud computing environments face persistent threats from credential compromise and insider risks. The Zero-Trust Architecture (ZTA) paradigm enforces continuous verification and least-privilege access across distributed cloud workloads. This paper provides a comprehensive evaluation of micro-segmentation, identity-aware proxies, and automated posture verification across multi-cloud enterprise deployments.",
                        "Cloud Security, Zero Trust Architecture, Access Control, Micro-segmentation, Identity Management",
                        2024,
                        "Cloud Computing",
                        "https://doi.org/10.1109/MCC.2024.3129580"
                ));

                paperRepository.save(new ResearchPaper(
                        "Quantitative Cybersecurity Risk Assessment for Critical Industrial Control Systems",
                        "Dr. Henrik Lindqvist, Maria Santos",
                        "Industrial Control Systems (ICS) and SCADA infrastructure are increasingly targeted by state-sponsored cyber adversaries. This research proposes a quantitative risk assessment methodology integrating fault tree analysis with Bayesian network modeling. The framework evaluates vulnerability chains in industrial PLCs, quantifies operational disruption risk, and prioritizes remediation investments based on economic impact metrics.",
                        "Risk Assessment, Industrial Control Systems, SCADA, Cybersecurity Risk, Vulnerability Analysis",
                        2022,
                        "Cybersecurity",
                        "https://doi.org/10.1016/j.ress.2022.108420"
                ));

                paperRepository.save(new ResearchPaper(
                        "Behavioral Malware Detection in Encrypted Network Traffic Using Flow Analytics",
                        "Kevin O'Connor, Dr. Yuto Takahashi",
                        "The proliferation of TLS encryption masks malicious communications, rendering payload-inspection tools ineffective. This study introduces a behavioral traffic profiling mechanism that extracts features from packet length distributions, inter-arrival times, and TLS handshake metadata. Using ensemble machine learning classifiers, our method successfully identifies malware command-and-control (C2) channels operating inside encrypted tunnels without decrypting payload contents.",
                        "Malware Detection, Encrypted Traffic, Traffic Flow Analysis, TLS Handshake, Machine Learning",
                        2024,
                        "Malware Analysis",
                        "https://doi.org/10.1109/TIFS.2024.3354120"
                ));

                paperRepository.save(new ResearchPaper(
                        "Real-Time Anomaly Detection in High-Speed Enterprise Networks using Streaming Analytics",
                        "Dr. Sarah Lin, Victor Hugo, Alex Rivera",
                        "High-speed 100Gbps enterprise backbones process billions of network events daily, creating significant computational bottlenecks for security monitoring systems. We propose a distributed streaming anomaly detection pipeline built on sliding-window entropy analysis and sketch algorithm data structures. The system detects volumetric DDoS attacks and port scanning activity in sub-second latency while maintaining low memory consumption.",
                        "Anomaly Detection, Network Traffic, Streaming Analytics, Real-Time Monitoring, DDoS Detection",
                        2023,
                        "Network Security",
                        "https://doi.org/10.1016/j.future.2023.106512"
                ));

                paperRepository.save(new ResearchPaper(
                        "Differential Privacy Frameworks for Smart Wearable and Health Monitoring Devices",
                        "Dr. Beatrice Dupont, Liam Thorne",
                        "Smart health monitoring devices continuously collect sensitive biometric data, introducing severe personal privacy risks when telemetry is transmitted to cloud servers. This research formulates a localized differential privacy (LDP) framework tailored for real-time wearable sensor streams. The mechanism injects controlled noise at the sensor level, ensuring mathematical privacy guarantees while preserving downstream diagnostic utility for healthcare providers.",
                        "Privacy Protection, Smart Devices, Differential Privacy, Healthcare Wearables, Data Confidentiality",
                        2025,
                        "Data Privacy",
                        "https://doi.org/10.1109/TDSC.2025.3401980"
                ));

                paperRepository.save(new ResearchPaper(
                        "Blockchain-Enabled Decentralized Identity Management for Secure IoT Ecosystems",
                        "Taro Tanaka, Dr. Rebecca Vance",
                        "Centralized identity management systems in IoT deployment introduce single points of failure and vulnerability to credential harvesting. This paper presents a decentralized self-sovereign identity (SSI) protocol built on a lightweight permissioned blockchain ledger. Smart contracts automate device registration, public key verification, and revoked credential management without reliance on third-party certificate authorities.",
                        "Blockchain, Decentralized Identity, IoT Ecosystems, Smart Contracts, Cryptographic Verification",
                        2024,
                        "Blockchain Security",
                        "https://doi.org/10.1016/j.bcra.2024.100512"
                ));

                paperRepository.save(new ResearchPaper(
                        "Next-Generation Intrusion Detection Systems: AI-Driven Feature Selection Techniques",
                        "Dr. Jonathan Blake, Claire Moreau",
                        "High dimensionality in network intrusion datasets degrades classification speed and increases false alarm rates in modern IDS solutions. We investigate metaheuristic feature selection techniques, including Particle Swarm Optimization (PSO) and Genetic Algorithms, to isolate optimal feature subsets. Our empirical results demonstrate a 40% reduction in processing latency and enhanced detection sensitivity across complex multi-vector cyber attacks.",
                        "Intrusion Detection Systems, Feature Selection, Optimization, IDS, Network Security",
                        2023,
                        "Network Security",
                        "https://doi.org/10.1109/TEVC.2023.3298104"
                ));

                paperRepository.save(new ResearchPaper(
                        "Lightweight Post-Quantum Cryptography for Resource-Constrained Embedded Devices",
                        "Dr. Hans Weber, Priya Sharma",
                        "Quantum computing poses existential threats to traditional public-key cryptosystems such as RSA and ECC. This paper evaluates lattice-based post-quantum cryptography (PQC) candidate algorithms optimized for 8-bit and 32-bit microcontroller architectures. We benchmark memory footprint, cycle count, and energy consumption, establishing optimal parameters for post-quantum IoT security transition.",
                        "Lightweight Cryptography, Post-Quantum Cryptography, Embedded Devices, Lattice Cryptography, IoT",
                        2026,
                        "Cryptography",
                        "https://doi.org/10.1016/j.jcsa.2026.100112"
                ));

                paperRepository.save(new ResearchPaper(
                        "Adversarial Robustness in Cyber Threat Intelligence and Automated Malware Classification",
                        "Dr. Marcus Vance, Dmitry Sokolov",
                        "Machine learning malware classifiers are vulnerable to adversarial evasion attacks where subtle binary perturbations bypass detection rules. This study proposes an adversarial training framework using generative perturbation models to fortify deep malware classifiers. Experiments reveal substantial robustness gains against code obfuscation and API call sequences manipulation.",
                        "Adversarial Robustness, Cyber Threat Intelligence, Malware Classification, Obfuscation, Machine Learning",
                        2025,
                        "Malware Analysis",
                        "https://doi.org/10.1109/TNNLS.2025.3411200"
                ));
            }
        };
    }
}
