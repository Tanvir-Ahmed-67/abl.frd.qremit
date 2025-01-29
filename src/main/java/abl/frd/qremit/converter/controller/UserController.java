package abl.frd.qremit.converter.controller;
import abl.frd.qremit.converter.model.ExchangeHouseModel;
import abl.frd.qremit.converter.helper.MyUserDetails;
import abl.frd.qremit.converter.model.Role;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.service.CommonService;
import abl.frd.qremit.converter.service.ExchangeHouseModelService;
import abl.frd.qremit.converter.service.MyUserDetailsService;
import abl.frd.qremit.converter.service.RoleModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.persistence.EntityManager;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
@SuppressWarnings("unchecked")
@Controller
public class UserController {
    private final MyUserDetailsService myUserDetailsService;
    private final ExchangeHouseModelService exchangeHouseModelService;
    private final RoleModelService roleModelService;
    private PasswordEncoder passwordEncoder;
    private final CommonService commonService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    public UserController(MyUserDetailsService myUserDetailsService, ExchangeHouseModelService exchangeHouseModelService, RoleModelService roleModelService, PasswordEncoder passwordEncoder, CommonService commonService) {
        this.myUserDetailsService = myUserDetailsService;
        this.exchangeHouseModelService = exchangeHouseModelService;
        this.roleModelService = roleModelService;
        this.passwordEncoder = passwordEncoder;
        this.commonService = commonService;
    }
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }
    @RequestMapping("/login")
    public String loginPage(){
        return "auth-login";
    }    
    @RequestMapping("/super-admin-home-page")
    public String loginSubmitSuperAdmin(){ return "layouts/dashboard"; }
    @RequestMapping("/admin-home-page")
    public String loginSubmitAdmin(){ return "layouts/dashboard"; }
    @RequestMapping("/user-home-page")
    public String loginSubmitUser(@AuthenticationPrincipal MyUserDetails userDetails, Model model){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        return "layouts/dashboard"; 
    }
    @RequestMapping("/change-password")
    public String showChangePasswordPage() {
        return "pages/user/userPasswordChangeForm";
    }
    @RequestMapping(value="/change-password-for-first-time-login", method = RequestMethod.POST)
    public String changePassword(@RequestParam("password") String newPassword, @AuthenticationPrincipal MyUserDetails userDetails) {
        User user = myUserDetailsService.loadUserByUserEmail(userDetails.getUserEmail());
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangeRequired(false);
        myUserDetailsService.updatePasswordForFirstTimeUserLogging(user);
        return "redirect:/login";
    }

    @RequestMapping("/home")
    public String loginSubmit(){
        return "layouts/dashboard";
    }
    @RequestMapping("/logout")
    public String logoutSuccessPage(){
        return "auth-login";
    }
    @RequestMapping("/allUsers")
    public String loadAllUser(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<User> userList;
        List<User> adminList;
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (authorityName.equals("ROLE_SUPERADMIN")) {
                userList = myUserDetailsService.loadUsersOnly();
                adminList = myUserDetailsService.loadAdminsOnly();
                model.addAttribute("UserList", userList);
                model.addAttribute("adminList", adminList);
                return "pages/superAdmin/superAdminUserListPage";
            }
            if (authorityName.equals("ROLE_ADMIN")) {
                userList = myUserDetailsService.loadUsersOnly();
                model.addAttribute("UserList", userList);
                return "pages/admin/adminUserListPage";
            }
        }
        return "allUsers";
    }

    @GetMapping(value ="/getAllUsers", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllUsers(Model model){
        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (authorityName.equals("ROLE_SUPERADMIN") || authorityName.equals("ROLE_ADMIN")){
                List<Object[]> resultList = myUserDetailsService.loadAllUsersAndRoles();
                int i = 1;
                for(Object[] result: resultList){
                    Map<String, Object> dataMap = new HashMap<>();
                    User user = (User) result[0];
                    Role role = (Role) result[1];
                    String roleName = role.getRoleName();
                    int userId = user.getId();
                    if(authorityName.equals("ROLE_SUPERADMIN")){
                        if(roleName.equals("ROLE_SUPERADMIN"))  continue;
                    }
                    if (authorityName.equals("ROLE_ADMIN")){
                        if(roleName.equals("ROLE_SUPERADMIN"))  continue;
                        if(roleName.equals("ROLE_ADMIN"))  continue;
                    }
                    String exchangeCode = user.getExchangeCode();
                    
                    if(!CommonService.checkEmptyString(exchangeCode)) exchangeCode = exchangeCode.replace(",", ", ");
                    String status = (user.getActiveStatus() == true) ? "Active": CommonService.generateClassForText("Inactive","text-danger fw-bold");
                    String btn = CommonService.generateTemplateBtn("template-editBtn.txt","/userEditForm/" + userId,"btn-info btn-sm edit_user text-white",String.valueOf(userId),"Edit");
                    btn += CommonService.generateTemplateBtn("template-viewBtn.txt","#","btn-danger btn-sm reset_pass",String.valueOf(userId),"Reset Password");
                    String action = CommonService.generateTemplateBtn("template-btngroup.txt", "#", "", "", btn);
                    
                    dataMap.put("sl", i++);
                    dataMap.put("email", user.getUserEmail());
                    dataMap.put("userName", user.getUserName());
                    dataMap.put("exchangeCode", exchangeCode);
                    dataMap.put("status", status);
                    dataMap.put("role", roleName.replace("ROLE_", ""));
                    dataMap.put("action", action);
                    dataList.add(dataMap);
                }
                resp.put("data",dataList);
            }else resp = CommonService.getResp(1, "You are not allowed to access this page", null);
        }
        return ResponseEntity.ok(resp);
    }
    
    @GetMapping(value = "/getChartData", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getChartData() {
        // SQL query to fetch data for all years
        // String query = "SELECT year, month_name, CAST(abl_amount AS DOUBLE) AS abl_amount " +
        //                "FROM analytics_abl_growth " +
        //                "ORDER BY year, id";

                    String query = "SELECT year, month_name, CAST(abl_amount AS DOUBLE) AS abl_amount FROM analytics_abl_growth WHERE year > 2013 ORDER BY year, id";
        // Execute query
        List<Object[]> resultList = entityManager.createNativeQuery(query).getResultList();

        // Transform results
        Map<String, Map<String, Double>> yearDataMap = new HashMap<>();
        List<String> labels = new ArrayList<>();
        for (Object[] row : resultList) {
            String year = String.valueOf(row[0]); // Year
            String month = (String) row[1];      // Month Name
            Double amount = (Double) row[2];     // National Amount

            // Add unique months to labels
            if (!labels.contains(month)) {
                labels.add(month);
            }

            // Organize data by year
            yearDataMap.putIfAbsent(year, new HashMap<>());
            yearDataMap.get(year).put(month, amount);
        }

        // Prepare datasets for Chart.js
        List<Map<String, Object>> datasets = new ArrayList<>();
        String[] colors = {"#54a0ff", "#ff6b6b", "#1dd1a1", "#feca57", "#5f27cd", "#c8d6e5"};
        int colorIndex = 0;
        for (String year : yearDataMap.keySet()) {
            Map<String, Double> monthData = yearDataMap.get(year);

            List<Double> data = new ArrayList<>();
            for (String month : labels) {
                data.add(monthData.getOrDefault(month, 0.0));
            }

            Map<String, Object> dataset = new HashMap<>();
            dataset.put("year", year);
            dataset.put("data", data);
            dataset.put("backgroundColor", colors[colorIndex % colors.length] + "80"); // Transparent color
            dataset.put("borderColor", colors[colorIndex % colors.length]); // Solid color
            datasets.add(dataset);
            colorIndex++;
        }

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("datasets", datasets);

        return response;
    }



    @GetMapping(value = "/getTargetAchievementData", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getTargetAchievementData() {
        // SQL query with CAST to convert VARCHAR fields to DOUBLE
        String query = "SELECT year, CAST(target AS DOUBLE) AS target, " +
                    "CAST(achievement AS DOUBLE) AS achievement, " +
                    "CAST(percentage AS DOUBLE) AS percentage " +
                    "FROM analytics_abl_target_achievement ORDER BY year";

        // Execute the query
        List<Object[]> resultList = entityManager.createNativeQuery(query).getResultList();

        // Prepare response data
        List<String> labels = new ArrayList<>();
        List<Double> targets = new ArrayList<>();
        List<Double> achievements = new ArrayList<>();
        List<Double> percentages = new ArrayList<>();

        for (Object[] row : resultList) {
            labels.add(String.valueOf(row[0])); // year
            targets.add((Double) row[1]);      // target
            achievements.add((Double) row[2]); // achievement
            percentages.add((Double) row[3]);  // percentage
        }

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("targets", targets);
        response.put("achievements", achievements);
        response.put("percentages", percentages);

        return response;
    }


    @GetMapping(value = "/getBankRemittanceData", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getBankRemittanceData() {
        // SQL Query
        String query = "SELECT bank_name, year, SUM(amount) AS total_amount " +
                       "FROM analytics_all_bank_remittance " +
                       "GROUP BY bank_name, year " +
                       "ORDER BY bank_name, year";

        // Execute query
        List<Object[]> resultList = entityManager.createNativeQuery(query).getResultList();

        // Transform results
        Map<String, Map<String, Double>> bankDataMap = new LinkedHashMap<>();
        Set<String> years = new TreeSet<>(); // Maintain ordered unique years

        for (Object[] row : resultList) {
            String bankName = (String) row[0];
            String year = String.valueOf(row[1]);
            Double amount = ((Number) row[2]).doubleValue();

            years.add(year);
            bankDataMap.putIfAbsent(bankName, new LinkedHashMap<>());
            bankDataMap.get(bankName).put(year, amount);
        }

        // Prepare datasets for Chart.js
        List<Map<String, Object>> datasets = new ArrayList<>();
        List<String> yearLabels = new ArrayList<>(years);
        String[] colors = {"#54a0ff", "#ff6b6b", "#1dd1a1", "#feca57", "#5f27cd", "#c8d6e5"};
        int colorIndex = 0;

        for (Map.Entry<String, Map<String, Double>> entry : bankDataMap.entrySet()) {
            String bankName = entry.getKey();
            Map<String, Double> yearData = entry.getValue();

            List<Double> data = new ArrayList<>();
            for (String year : yearLabels) {
                data.add(yearData.getOrDefault(year, 0.0));
            }

            Map<String, Object> dataset = new HashMap<>();
            dataset.put("label", bankName);
            dataset.put("data", data);
            dataset.put("backgroundColor", colors[colorIndex % colors.length] + "80"); // Transparent color
            dataset.put("borderColor", colors[colorIndex % colors.length]); // Solid color
            dataset.put("borderWidth", 1);
            datasets.add(dataset);

            colorIndex++;
        }

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("labels", yearLabels);
        response.put("datasets", datasets);

        return response;
    } 

    @GetMapping(value = "/getBankRemittanceDataByYear", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getBankRemittanceDataByYear() {
          // SQL Query
          String query = "SELECT bank_name, year, SUM(amount) AS total_amount " +
          "FROM analytics_all_bank_remittance " +
          "GROUP BY bank_name, year " +
          "ORDER BY bank_name, year";

            // Execute query
            List<Object[]> resultList = entityManager.createNativeQuery(query).getResultList();

            // Transform results
            Map<String, Map<String, Double>> bankDataMap = new LinkedHashMap<>();
            Set<String> years = new TreeSet<>(); // Maintain ordered unique years
            Set<String> banks = new LinkedHashSet<>(); // Maintain ordered unique banks

            for (Object[] row : resultList) {
            String bankName = (String) row[0];
            String year = String.valueOf(row[1]);
            Double amount = ((Number) row[2]).doubleValue();

            years.add(year);
            banks.add(bankName);

            bankDataMap.putIfAbsent(bankName, new LinkedHashMap<>());
            bankDataMap.get(bankName).put(year, amount);
            }

            // Prepare datasets for Chart.js
            List<Map<String, Object>> datasets = new ArrayList<>();
            List<String> yearLabels = new ArrayList<>(years);
            String[] colors = {"#54a0ff", "#ff6b6b", "#1dd1a1", "#feca57", "#5f27cd", "#c8d6e5"};
            int colorIndex = 0;

            for (String year : yearLabels) {
            List<Double> data = new ArrayList<>();
            for (String bank : banks) {
            Map<String, Double> yearData = bankDataMap.getOrDefault(bank, Collections.emptyMap());
            data.add(yearData.getOrDefault(year, 0.0));
            }

            Map<String, Object> dataset = new HashMap<>();
            dataset.put("label", year);
            dataset.put("data", data);
            dataset.put("backgroundColor", colors[colorIndex % colors.length] + "80"); // Transparent color
            dataset.put("borderColor", colors[colorIndex % colors.length]); // Solid color
            dataset.put("borderWidth", 1);
            datasets.add(dataset);

            colorIndex++;
            }

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("labels", new ArrayList<>(banks)); // X-axis banks
            response.put("datasets", datasets);

            return response;
    }

    @GetMapping(value = "/getBankRemittanceDataForTable", produces = "application/json")
    @ResponseBody
    public List<Map<String, Object>> getBankRemittanceDataForTable() {
        // SQL Query
        String query = "SELECT bank_name, year, SUM(amount) AS total_amount " +
                    "FROM analytics_all_bank_remittance " +
                    "GROUP BY bank_name, year " +
                    "ORDER BY bank_name, year";

        // Execute query
        List<Object[]> resultList = entityManager.createNativeQuery(query).getResultList();

        // Transform results
        List<Map<String, Object>> response = new ArrayList<>();
        for (Object[] row : resultList) {
            Map<String, Object> record = new HashMap<>();
            record.put("bankName", row[0]); // Bank name
            record.put("year", row[1]);    // Year
            record.put("amount", ((Number) row[2]).doubleValue()); // Total amount
            response.add(record);
        }

        return response;
    }

    @GetMapping(value = "/getMonthlyAnalytics", produces = "application/json")
    public ResponseEntity<?> getMonthlyAnalytics() {
        try {
            // SQL query
            String query = "SELECT month_name, target, achievement " +
                           "FROM analytics_abl_current_year_target_achievement " +
                           "ORDER BY id";

            // Execute query
            List<Object[]> resultList = entityManager.createNativeQuery(query).getResultList();

            // Process results into structured data
            List<String> labels = new ArrayList<>(); // Month names
            List<Double> targets = new ArrayList<>(); // Target values
            List<Double> achievements = new ArrayList<>(); // Achievement values

            for (Object[] row : resultList) {
                labels.add((String) row[0]); // Month name
                targets.add(Double.parseDouble((String) row[1])); // Convert target to Double
                achievements.add(Double.parseDouble((String) row[2])); // Convert achievement to Double
            }

            // Prepare the response
            Map<String, Object> response = new HashMap<>();
            response.put("labels", labels); // Y-axis (Month names)
            response.put("targets", targets); // X-axis (Target)
            response.put("achievements", achievements); // X-axis (Achievement)

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching analytics data.");
        }
    }

    
    @RequestMapping("/newUserCreationForm")
    public String showUserCreateFromAdmin(Model model){
        model.addAttribute("user", new User());
        List<ExchangeHouseModel> exchangeHouseList;
        exchangeHouseList = exchangeHouseModelService.loadAllActiveExchangeHouse();
        model.addAttribute("exchangeList", exchangeHouseList);
        return "pages/admin/adminNewUserEntryForm";
    }

    @RequestMapping(value = "/createNewUser", method = RequestMethod.POST)
    public String submitUserCreateFromSuperAdmin(User user, RedirectAttributes ra){
        Role role = roleModelService.findRoleByRoleName("ROLE_USER");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActiveStatus(false);
        user.setPasswordChangeRequired(true);
        user.setRoles(roleSet);
        myUserDetailsService.insertUser(user);
        ra.addFlashAttribute("message","New User has been created successfully");
        return "redirect:/allUsers";
    }
    @GetMapping("/adminDashboard")
    @ResponseBody
    public List<Integer> loadAdminDashboard(Model model){
        List<Integer> count = commonService.CountAllFourTypesOfData();
        return count;
    }
    @RequestMapping(value="/userEditForm/{id}", method = RequestMethod.GET)
    public String showUserEditForm(Model model, @PathVariable(required = true, name= "id") int id){
        User userSelected = myUserDetailsService.loadUserByUserId(id);
        List<ExchangeHouseModel> exchangeHouseList;
        String[] exchangeCodeAssignedToUser = userSelected.getExchangeCode().split(",");
        exchangeHouseList = exchangeHouseModelService.loadAllActiveExchangeHouse();
        model.addAttribute("exchangeList", exchangeHouseList);
        model.addAttribute("user", userSelected);
        model.addAttribute("exchangeCodeAssignedToUser", exchangeCodeAssignedToUser);
        return "pages/admin/adminUserEditForm";
    }

    @RequestMapping(value="/editUser/{id}", method= RequestMethod.POST)
    public String editExchangeHouse(Model model, @PathVariable(required = true, name= "id") String id, @Valid User user, BindingResult result, RedirectAttributes ra){
        int idInIntegerFormat = CommonService.convertStringToInt(id);
        if (user.getAllowedIps() != null) {
            String formattedIps = user.getAllowedIps().replace("\n", ",").trim();
            formattedIps = formattedIps.replaceAll(",+", ","); // Replace multiple commas with a single comma
            user.setAllowedIps(formattedIps);
        }
        if (result.hasErrors()) {
            user.setId(idInIntegerFormat);
            ra.addFlashAttribute("message","Error Occurred: User Update Failed !!");
            return "redirect:/allUsers";
        }
        try {
            String startTime = user.getStartTime();
            if (startTime != null && startTime.matches("^\\d{2}:\\d{2}$")) {
                startTime = startTime + ":00";
            }
            String endTime = user.getEndTime();
            if (endTime != null && endTime.matches("^\\d{2}:\\d{2}$")) {
                endTime = endTime + ":00";
            }
            user.setStartTime(startTime);
            user.setEndTime(endTime);
            myUserDetailsService.editUser(user);
            ra.addFlashAttribute("message","User Updated successfully");
            model.addAttribute("user",user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/adminReport?type=6";
    }

    @RequestMapping("/showInactiveUsers")
    public String showInactiveUserSuperAdmin(Model model){
        List<User> inactiveUserModelList;
        inactiveUserModelList = myUserDetailsService.loadAllInactiveUsers();
        model.addAttribute("inactiveUserModelList", inactiveUserModelList);
        return "pages/superAdmin/superAdminInactiveUserListPage";
    }

    @RequestMapping(value="/activateUser/{id}", method = RequestMethod.POST)
    public String activateInactiveUser(Model model, @PathVariable(required = true, name = "id") String id, RedirectAttributes ra) {
        int idInIntegerFormat = CommonService.convertStringToInt(id);
        if(myUserDetailsService.updateInactiveUser(idInIntegerFormat)){
            ra.addFlashAttribute("message","User has been activated successfully");
        }
        return "redirect:/showInactiveUsers";
    }
    
    @GetMapping("/userFileUploadReport")
    public String userFileUploadReport(@AuthenticationPrincipal MyUserDetails userDetails,Model model, @RequestParam(defaultValue = "") String type){
        model.addAttribute("exchangeMap", myUserDetailsService.getLoggedInUserMenu(userDetails));
        //List<Map<String, String>> reportColumn = ReportController.getReportColumn(type);
        return "pages/user/userFileUploadReport";
    }

    @GetMapping("/adminReport")
    public String adminFileUploadReport(@AuthenticationPrincipal MyUserDetails userDetails,Model model, @RequestParam(defaultValue = "") String type){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
        Map<String, Object> userData = myUserDetailsService.getLoggedInUserDetails(authentication, myUserDetails);
        if(userData.get("status") == HttpStatus.UNAUTHORIZED)   return HttpStatus.UNAUTHORIZED.getReasonPhrase();
        int userId = (int) userData.get("userid");
        Map<String, String> exchangeMap = new HashMap<>();
        if(userData.containsKey("exchangeMap")) exchangeMap = (Map<String, String>) userData.get("exchangeMap");
        model.addAttribute("exchangeMap", exchangeMap);
        String sidebar = CommonService.getSidebarNameByUserid(userId);
        model.addAttribute("sidebar", sidebar);
        return "pages/admin/adminReport";
    }

    @GetMapping("/showTimePickerForm")
    public String showTimePickerForm(Model model){
        return "pages/admin/showTimePickerForm";
    }
    @PostMapping("/setLoginTimeRestrictionsForAllUsers")
    public String setLoginTimeRestrictionsForAllUsers(@RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime, RedirectAttributes redirectAttributes){
        String errorMessage = "You must be logged in to access this page.";
        // Check if the user is loggedIn or authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            // Redirect to the login page with an error message
            try {
                errorMessage = URLEncoder.encode(errorMessage, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return "redirect:/login?error=" + errorMessage;
        }
        // After confirming successful authentication, start the time setting process
        try {
            if (startTime != null && startTime.matches("^\\d{2}:\\d{2}$")) {
                startTime = startTime + ":00";
            }
            if (endTime != null && endTime.matches("^\\d{2}:\\d{2}$")) {
                endTime = endTime + ":00";
            }
            boolean success = myUserDetailsService.setLoginTimeRestrictionsForAllUsers(startTime, endTime);
            if (success) {
                redirectAttributes.addFlashAttribute("message", "Login time restrictions updated successfully!");
                redirectAttributes.addFlashAttribute("messageType", "success");
            } else {
                redirectAttributes.addFlashAttribute("message", "Failed to update login time restrictions.");
                redirectAttributes.addFlashAttribute("messageType", "error");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An error occurred: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/showTimePickerForm";
    }

    @RequestMapping(value="/resetPassword", produces="application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> resetPassword(@AuthenticationPrincipal MyUserDetails userDetails,Model model, @RequestParam("id") String id){
        Map<String, Object> resp = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails)authentication.getPrincipal();
        Map<String, Object> userData = myUserDetailsService.getLoggedInUserDetails(authentication, myUserDetails);
        if(userData.get("status") == HttpStatus.UNAUTHORIZED) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Map<String, Integer> role = (Map<String, Integer>) userData.get("role");
        if((Integer) role.get("isAdmin") != 1)  return ResponseEntity.ok(CommonService.getResp(0, "You are not allowed to perform this operation", null));
        String password = passwordEncoder.encode("12345");
        User user = myUserDetailsService.loadUserByUserId(CommonService.convertStringToInt(id));
        if(user == null)    return ResponseEntity.ok(CommonService.getResp(1, "No user data found using following id", null));
        int rowsUpdated = myUserDetailsService.resetPassword(user.getId(), password);
        if(rowsUpdated > 0) resp = CommonService.getResp(0, "Password resetted successfully", null);
        else resp = CommonService.getResp(1, "Error updating for password reset", null);
        return ResponseEntity.ok(resp);
    }
}