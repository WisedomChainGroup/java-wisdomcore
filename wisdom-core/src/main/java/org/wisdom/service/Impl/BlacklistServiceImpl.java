package org.wisdom.service.Impl;

import org.springframework.stereotype.Service;
import org.wisdom.service.BlacklistService;

import java.util.Arrays;
import java.util.List;

@Service
public class BlacklistServiceImpl implements BlacklistService {

    @Override
    public boolean checkInBlacklist(String address) {
        String blackListStr =
                "WX1Bf1vqYL5TW6kKoERZSETqKpDWcD5yuzNM," +
                "WX16XnRe93exvm8tt8LEvBK1ciDu9DUp2aZk," +
                "WX1LqSFNVYCVfqeWJ27X6VM4rV6r6egDuvaL," +
                "WX1LyWziMBNvxth5a1qPuGBzTYgvfwYwbD2k," +
                "WX1NdGqT3YmAMygREZg4DZSAytRbfjXqvwYh," +
                "WX1F6Xf8toEfWLFjXgmihMz21Grd9XYgeSve," +
                "WX12BiWxToZB8UPsfFPAzQxQ7TzyEu1kUWeq," +
                "WX1Mq8BxJ8gu9QbuqnmvyYkyUytzsPz736A2," +
                "WX14NqYZsKQ7dDZpfG8VUsh6rDG3BUWro9t9," +
                "WX1LgdYa3MFBZGzMVia3bTUyEFKL6sJJjqKg," +
                "WX1PzvixeChm1JSwTS6cFY1r54nHZBQ4nSks," +
                "WX148XCjc23TJJLLzX9v6nNWoVNTC1uq3hns," +
                "WX1G7JrdxYppf7w9PGWpY35dzs9igxBY2Db9," +
                "WX1CUFDhQ3T62JHKNfbYsxo3juiLAkwdpwXy," +
                "WX1HiRg4xZ4zLBJpvmsDALhPXSX48KjpTBrZ";
        
        List<String> blackList = Arrays.asList(blackListStr.split(","));
        return blackList.contains(address);
    }
}
